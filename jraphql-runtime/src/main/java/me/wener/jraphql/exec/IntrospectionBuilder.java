package me.wener.jraphql.exec;

import com.github.wenerme.wava.util.JSON;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import me.wener.jraphql.exec.Introspection.Directive;
import me.wener.jraphql.exec.Introspection.DirectiveLocation;
import me.wener.jraphql.exec.Introspection.EnumValue;
import me.wener.jraphql.exec.Introspection.Field;
import me.wener.jraphql.exec.Introspection.Type;
import me.wener.jraphql.exec.Introspection.TypeKind;
import me.wener.jraphql.lang.DirectiveDefinition;
import me.wener.jraphql.lang.EnumTypeDefinition;
import me.wener.jraphql.lang.EnumValueDefinition;
import me.wener.jraphql.lang.FieldDefinition;
import me.wener.jraphql.lang.InputObjectTypeDefinition;
import me.wener.jraphql.lang.InputValueDefinition;
import me.wener.jraphql.lang.InterfaceTypeDefinition;
import me.wener.jraphql.lang.Langs;
import me.wener.jraphql.lang.Node;
import me.wener.jraphql.lang.ObjectTypeDefinition;
import me.wener.jraphql.lang.TypeDefinition;
import me.wener.jraphql.lang.UnionTypeDefinition;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/12
 */
public class IntrospectionBuilder {

  private final TypeSystemDocument typeSystemDocument;

  @Getter private Introspection.Schema schema;
  private Map<String, Type> types = Maps.newHashMap();
  private List<Directive> directives = Lists.newArrayList();
  private Multimap<String, String> subtypes = HashMultimap.create();

  public IntrospectionBuilder(TypeSystemDocument typeSystemDocument) {
    this.typeSystemDocument = typeSystemDocument;
    buildSchema(typeSystemDocument);
  }

  Introspection.Schema buildSchema(TypeSystemDocument typeSystemDocument) {

    // prepare
    for (TypeDefinition definition : typeSystemDocument.getDefinitions().values()) {
      switch (definition.getKind()) {
        case OBJECT:
          subtypes.putAll(
              definition.getName(), definition.unwrap(ObjectTypeDefinition.class).getInterfaces());
          break;
      }
    }

    // build types
    for (TypeDefinition definition : typeSystemDocument.getDefinitions().values()) {
      switch (definition.getKind()) {
        case SCHEMA:
          continue;
        case DIRECTIVE:
          continue;
        default:
          types.put(definition.getName(), buildType(definition));
      }
    }
    // build fields
    for (TypeDefinition definition : typeSystemDocument.getDefinitions().values()) {
      switch (definition.getKind()) {
        case SCHEMA:
          continue;
        case DIRECTIVE:
          directives.add(buildDirective(definition.unwrap(DirectiveDefinition.class)));
          break;
        default:
          // field will reference to type
          buildTypeRef(definition);
      }
    }
    schema = new Introspection.Schema();
    schema
        .setDirectives(directives)
        .setTypes(ImmutableList.copyOf(types.values()))
        .setQueryType(requireType(typeSystemDocument.getQueryTypeDefinition().getName()))
        .setMutationType(
            Optional.ofNullable(typeSystemDocument.getMutationTypeDefinition())
                .map(ObjectTypeDefinition::getName)
                .map(this::requireType)
                .orElse(null))
        .setSubscriptionType(
            Optional.ofNullable(typeSystemDocument.getSubscriptionTypeDefinition())
                .map(ObjectTypeDefinition::getName)
                .map(this::requireType)
                .orElse(null));

    return schema;
  }

  Directive buildDirective(DirectiveDefinition definition) {
    Directive v = new Directive();
    v.setName(definition.getName())
        .setLocations(
            definition
                .getLocations()
                .stream()
                .map(l -> DirectiveLocation.valueOf(l))
                .collect(Collectors.toList()))
        .setDescription(findDescription(definition.getDescription(), definition))
        .setArgs(buildInputValues(definition.getArgumentDefinitions()));
    return v;
  }

  List<Introspection.InputValue> buildInputValues(List<InputValueDefinition> values) {
    return values.stream().map(this::buildInputValue).collect(Collectors.toList());
  }

  Introspection.InputValue buildInputValue(InputValueDefinition definition) {
    return new Introspection.InputValue()
        .setName(definition.getName())
        .setDescription(findDescription(definition.getDescription(), definition))
        .setType(buildType(definition.getType()))
        .setDefaultValue(
            definition.getDefaultValue() == null
                ? null
                : JSON.stringify(definition.getDefaultValue().getValue())) // TODO JSON ?
    ;
  }

  List<Introspection.Field> buildFields(List<FieldDefinition> values) {
    return values.stream().map(this::buildField).collect(Collectors.toList());
  }

  Introspection.Field buildField(FieldDefinition definition) {
    Field builder = new Field();
    builder
        .setName(definition.getName())
        .setDescription(findDescription(definition.getDescription(), definition))
        .setDeprecated(false);
    findDeprecated(definition.getDirectives())
        .ifPresent(v -> builder.setDeprecated(true).setDeprecationReason(v));
    builder
        .setType(buildType(definition.getType()))
        .setArgs(buildInputValues(definition.getArgumentDefinitions()));
    return builder;
  }

  private Optional<String> findDeprecated(List<me.wener.jraphql.lang.Directive> directives) {
    for (me.wener.jraphql.lang.Directive directive : directives) {
      if (Objects.equals(directive.getName(), "deprecated")) {
        Optional<String> reason =
            Langs.findByName(directive.getArguments(), "reason")
                .map(v -> String.valueOf(v.getValue().getValue()));
        if (!reason.isPresent()) {
          return Optional.of("No longer supported");
        } else {
          return reason;
        }
      }
    }
    return Optional.empty();
  }

  Type buildType(String type) {
    return requireType(type);
  }

  Type buildType(me.wener.jraphql.lang.Type type) {
    switch (type.getKind()) {
      case NAMED:
        // NOTE required type already build up
        return requireType(type.getName());
      case NON_NULL:
        return new Type().setKind(TypeKind.NON_NULL).setOfType(buildType(type.getType()));
      case LIST:
        return new Type().setKind(TypeKind.LIST).setOfType(buildType(type.getType()));
    }
    throw new AssertionError();
  }

  private Type requireType(String name) {
    return Objects.requireNonNull(types.get(name), "type not found: " + name);
  }

  Introspection.EnumValue buildEnumValue(EnumValueDefinition definition) {
    EnumValue builder =
        new EnumValue()
            .setName(definition.getName())
            .setDescription(findDescription(definition.getDescription(), definition))
            .setDeprecated(false);
    findDeprecated(definition.getDirectives())
        .ifPresent(v -> builder.setDeprecated(true).setDeprecationReason(v));
    return builder;
  }

  Type buildTypeRef(TypeDefinition definition) {
    Type type = requireType(definition.getName());
    switch (definition.getKind()) {
      case OBJECT:
        ObjectTypeDefinition object = definition.unwrap(ObjectTypeDefinition.class);
        type.setInterfaces(
            object.getInterfaces().stream().map(v -> buildType(v)).collect(Collectors.toList()));
        type.setFields(buildFields(object.getFieldDefinitions()));
        break;
      case INTERFACE:
        InterfaceTypeDefinition iface = definition.unwrap(InterfaceTypeDefinition.class);
        // Implements
        type.setPossibleTypes(
            types
                .values()
                .stream()
                .filter(v -> v.getInterfaces() != null && v.getInterfaces().contains(type))
                .collect(Collectors.toList()));
        type.setFields(buildFields(iface.getFieldDefinitions()));
        break;
      case INPUT_OBJECT:
        InputObjectTypeDefinition input = definition.unwrap(InputObjectTypeDefinition.class);
        type.setInputFields(buildInputValues(input.getInputFieldsDefinitions()));
        break;
      case UNION:
        UnionTypeDefinition union = definition.unwrap(UnionTypeDefinition.class);
        type.setPossibleTypes(
            union.getMemberTypes().stream().map(this::requireType).collect(Collectors.toList()));
        break;
    }
    return type;
  }

  String findDescription(String desc, Node node) {
    if (desc == null) {
      desc =
          node.getComments()
              .stream()
              .map(v -> v.getContent().trim())
              .collect(Collectors.joining("\n"));
      if (desc.isEmpty()) {
        desc = null;
      }
    }
    return desc;
  }

  Type buildType(TypeDefinition definition) {
    Type type = new Type();
    type.setName(definition.getName())
        .setDescription(findDescription(definition.getDescription(), definition));

    switch (definition.getKind()) {
      case SCALAR:
        type.setKind(TypeKind.SCALAR);
        break;
      case ENUM:
        type.setKind(TypeKind.ENUM);
        EnumTypeDefinition enu = definition.unwrap(EnumTypeDefinition.class);
        type.setEnumValues(
            enu.getEnumValueDefinitions()
                .stream()
                .map(this::buildEnumValue)
                .collect(Collectors.toList()));
        break;
      case OBJECT:
        type.setKind(TypeKind.OBJECT);
        break;
      case INTERFACE:
        InterfaceTypeDefinition iface = definition.unwrap(InterfaceTypeDefinition.class);
        type.setKind(TypeKind.INTERFACE);
        type.setPossibleTypes(
            subtypes
                .get(iface.getName())
                .stream()
                .map(this::requireType)
                .collect(Collectors.toList()));
        break;
      case UNION:
        type.setKind(TypeKind.UNION);
        break;
      case INPUT_OBJECT:
        type.setKind(TypeKind.INPUT_OBJECT);
        break;
    }
    return type;
  }
}
