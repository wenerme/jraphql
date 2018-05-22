package me.wener.jraphql.exec;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Introspection for GraphQL <br>
 * <b>NOTE</b> All data are mutable because they contain circular dependency
 *
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 09/03/2018
 */
public interface Introspection {

  /** Weave the types in schema, use after deserialize */
  static Schema resolveSchmeaType(Schema schema) {
    Map<String, Type> types = Maps.newHashMap();
    for (Type type : schema.getTypes()) {
      types.put(type.getName(), type);
    }

    if (schema.getQueryType() != null) {
      schema.setQueryType(types.get(schema.getQueryType().getName()));
    }
    if (schema.getMutationType() != null) {
      schema.setMutationType(types.get(schema.getMutationType().getName()));
    }
    if (schema.getSubscriptionType() != null) {
      schema.setSubscriptionType(types.get(schema.getSubscriptionType().getName()));
    }

    for (Type type : schema.getTypes()) {
      if (type.getFields() != null)
        for (Field field : type.getFields()) {
          for (InputValue value : field.getArgs()) {
            value.setType(Holder.resolveType(types, value.getType()));
          }
        }
      if (type.getInputFields() != null)
        for (InputValue value : type.getInputFields()) {
          value.setType(Holder.resolveType(types, value.getType()));
        }
      if (type.getPossibleTypes() != null)
        type.getPossibleTypes().replaceAll(v -> types.get(v.getName()));
      if (type.getInterfaces() != null)
        type.getInterfaces().replaceAll(v -> types.get(v.getName()));
    }
    return schema;
  }

  enum TypeKind {
    SCALAR,
    OBJECT,
    INTERFACE,
    UNION,
    ENUM,
    INPUT_OBJECT,
    LIST,
    NON_NULL,
  }

  enum DirectiveLocation {
    QUERY,
    MUTATION,
    SUBSCRIPTION,
    FIELD,
    FRAGMENT_DEFINITION,
    FRAGMENT_SPREAD,
    INLINE_FRAGMENT,
    SCHEMA,
    SCALAR,
    OBJECT,
    FIELD_DEFINITION,
    ARGUMENT_DEFINITION,
    INTERFACE,
    UNION,
    ENUM,
    ENUM_VALUE,
    INPUT_OBJECT,
    INPUT_FIELD_DEFINITION,
  }

  @Data
  class Schema {

    @NotNull private List<@NotNull Type> types;
    @NotNull private Type queryType;
    private Type mutationType;
    private Type subscriptionType;
    @NotNull private List<@NotNull Directive> directives;

    public Type getType(String name) {
      for (Type type : types) {
        if (Objects.equals(type.getName(), name)) {
          return type;
        }
      }
      return null;
    }
  }

  @Getter
  @Setter
  class Type {

    @NotNull private TypeKind kind;
    private String name;
    private String description;
    // OBJECT and INTERFACE only
    private List<@NotNull Field> fields;
    // OBJECT only
    private List<@NotNull Type> interfaces;
    // INTERFACE and UNION only
    private List<@NotNull Type> possibleTypes;
    // ENUM only
    private List<@NotNull EnumValue> enumValues;
    // INPUT_OBJECT only
    private List<@NotNull InputValue> inputFields;
    // NON_NULL and LIST only
    private Type ofType; // This will cause circular reference

    @Override
    public String toString() {
      return String.format("Type(%s %s)", kind, name);
    }
  }

  @Data
  @ToString(exclude = "type")
  @EqualsAndHashCode(exclude = "type")
  class Field {

    @NotNull private String name;
    private String description;
    @NotNull private List<@NotNull InputValue> args;
    @NotNull private Type type;

    @JsonProperty("isDeprecated")
    @NotNull
    private Boolean deprecated;

    private String deprecationReason;
  }

  @Data
  @ToString(exclude = "type")
  @EqualsAndHashCode(exclude = "type")
  class InputValue {

    @NotNull private String name;
    private String description;
    @NotNull private Type type;
    private String defaultValue;
  }

  @Data
  class EnumValue {

    @NotNull private String name;
    private String description;

    @JsonProperty("isDeprecated")
    @NotNull
    private Boolean deprecated;

    private String deprecationReason;
  }

  @Data
  class Directive {

    @NotNull private String name;
    private String description;
    @NotNull private List<@NotNull DirectiveLocation> locations;
    @NotNull private List<@NotNull InputValue> args;
  }

  final class Holder {
    private Holder() {}

    private static Type resolveType(Map<String, Type> types, Type type) {
      if (type.getOfType() != null) {
        return type.setOfType(resolveType(types, type.getOfType()));
      }
      if (type.getName() != null) {
        return types.get(type.getName());
      }
      return type;
    }
  }
}
