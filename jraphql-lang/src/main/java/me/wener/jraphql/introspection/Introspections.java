package me.wener.jraphql.introspection;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 09/03/2018
 */
public interface Introspections {

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

  @Getter
  @Setter
  class Schema {

    @NotNull private List<@NotNull Type> types;
    @NotNull private Type queryType;
    private Type mutationType;
    private Type subscriptionType;
    @NotNull private List<@NotNull Directive> directives;
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
    private Type ofType;
  }

  @Getter
  @Setter
  class Field {

    @NotNull private String name;
    private String description;
    @NotNull private List<@NotNull InputValue> args;
    @NotNull private Type type;
    @NotNull private Boolean isDeprecated;
    private String deprecationReason;
  }

  @Getter
  @Setter
  class InputValue {

    @NotNull private String name;
    private String description;
    @NotNull private Type type;
    private String defaultValue;
  }

  @Getter
  @Setter
  class EnumValue {

    @NotNull private String name;
    private String description;
    @NotNull private Boolean deprecated;
    private String deprecationReason;
  }

  @Getter
  @Setter
  class Directive {

    @NotNull private String name;
    private String description;
    @NotNull private List<@NotNull DirectiveLocation> locations;
    @NotNull private List<@NotNull InputValue> args;
  }
}
