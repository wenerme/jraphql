package me.wener.jraphql.exec;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import me.wener.jraphql.lang.Directive;
import me.wener.jraphql.lang.EnumTypeDefinition;
import me.wener.jraphql.lang.EnumTypeExtension;
import me.wener.jraphql.lang.FieldDefinition;
import me.wener.jraphql.lang.InputObjectTypeDefinition;
import me.wener.jraphql.lang.InputObjectTypeExtension;
import me.wener.jraphql.lang.InterfaceTypeDefinition;
import me.wener.jraphql.lang.InterfaceTypeExtension;
import me.wener.jraphql.lang.Langs;
import me.wener.jraphql.lang.Node;
import me.wener.jraphql.lang.ObjectTypeDefinition;
import me.wener.jraphql.lang.ObjectTypeExtension;
import me.wener.jraphql.lang.ScalarTypeDefinition;
import me.wener.jraphql.lang.ScalarTypeExtension;
import me.wener.jraphql.lang.TypeDefinition;
import me.wener.jraphql.lang.TypeExtension;
import me.wener.jraphql.lang.UnionTypeDefinition;
import me.wener.jraphql.lang.UnionTypeExtension;

/**
 * Helper methods to extend the types.
 *
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/8
 */
public interface TypeExtender {

  static ScalarTypeDefinition extend(
      ScalarTypeDefinition type, Iterable<ScalarTypeExtension> extensions) {
    return type;
  }

  static EnumTypeDefinition extend(
      EnumTypeDefinition type, Iterable<EnumTypeExtension> extensions) {
    return type;
  }

  static UnionTypeDefinition extend(
      UnionTypeDefinition type, Iterable<UnionTypeExtension> extensions) {
    return type;
  }

  static InputObjectTypeDefinition extend(
      InputObjectTypeDefinition type, Iterable<InputObjectTypeExtension> extensions) {
    return type;
  }

  static InterfaceTypeDefinition extend(
      InterfaceTypeDefinition type, Iterable<InterfaceTypeExtension> extensions) {
    return type;
  }

  static ObjectTypeDefinition extend(
      ObjectTypeDefinition type,
      Iterable<ObjectTypeExtension> extensions,
      Map<String, TypeDefinition> types) {
    ArrayList<Directive> directives = Lists.newArrayList(type.getDirectives());
    ArrayList<String> interfaces = Lists.newArrayList(type.getInterfaces());
    ArrayList<FieldDefinition> fieldDefinitions = Lists.newArrayList(type.getFieldDefinitions());

    extensions.forEach(
        v -> {
          directives.addAll(v.getDirectives());
          interfaces.addAll(v.getInterfaces());
          fieldDefinitions.addAll(v.getFieldDefinitions());

          if (v.getExtendByName() != null) {
            ObjectTypeDefinition definition =
                types.get(v.getExtendByName()).unwrap(ObjectTypeDefinition.class);
            directives.addAll(definition.getDirectives());
            interfaces.addAll(definition.getInterfaces());
            fieldDefinitions.addAll(definition.getFieldDefinitions());
          }
        });

    return type.toBuilder()
        .directives(directives)
        .interfaces(interfaces)
        .fieldDefinitions(fieldDefinitions)
        .build();
  }

  static boolean isExtendable(Node target, Node ext) {
    return Langs.isTypeDefinition(target)
        && Langs.isTypeExtension(ext)
        && Langs.getTypeDefinitionKind(target) == Langs.getTypeDefinitionKind(ext)
        && Objects.equals(target.getName(), ext.unwrap(TypeExtension.class).getExtendTypeName());
  }

  static TypeDefinition extend(
      TypeDefinition type,
      Iterable<? extends TypeExtension> extensions,
      Map<String, TypeDefinition> types) {
    switch (type.getKind()) {
      case SCALAR:
        return extend(
            type.unwrap(ScalarTypeDefinition.class), (Iterable<ScalarTypeExtension>) extensions);
      case ENUM:
        return extend(
            type.unwrap(EnumTypeDefinition.class), (Iterable<EnumTypeExtension>) extensions);
      case OBJECT:
        return extend(
            type.unwrap(ObjectTypeDefinition.class),
            (Iterable<ObjectTypeExtension>) extensions,
            types);
      case INTERFACE:
        return extend(
            type.unwrap(InterfaceTypeDefinition.class),
            (Iterable<InterfaceTypeExtension>) extensions);
      case UNION:
        return extend(
            type.unwrap(UnionTypeDefinition.class), (Iterable<UnionTypeExtension>) extensions);
      case INPUT_OBJECT:
        return extend(
            type.unwrap(InputObjectTypeDefinition.class),
            (Iterable<InputObjectTypeExtension>) extensions);
      case DIRECTIVE:
        // TODO extend directive can add more directive on directive
      case SCHEMA:
        // TODO extend all operate type
      default:
        throw new UnsupportedOperationException("unable to extend");
    }
  }
}
