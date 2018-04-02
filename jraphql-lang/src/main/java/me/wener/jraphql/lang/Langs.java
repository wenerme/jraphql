package me.wener.jraphql.lang;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 29/03/2018
 */
public interface Langs {

  //  static Optional<String> getName(Object o) {
  //    if (o instanceof HasName) {
  //      return Optional.of(((HasName) o).getName());
  //    }
  //    return Optional.empty();
  //  }

  static <T extends HasName> Optional<T> findByName(Iterable<T> list, String name) {
    if (list == null) {
      return Optional.empty();
    }
    for (T t : list) {
      if (t.getName().equals(name)) {
        return Optional.of(t);
      }
    }
    return Optional.empty();
  }

  static Value enumOf(String v) {
    return new EnumValue().setValue(v);
  }

  static Value variableOf(String v) {
    return new Variable().setValue(v);
  }

  static Value valueOf(Object v) {
    if (v == null) {
      return new NullValue();
    }
    Class<?> clazz = v.getClass();
    if (clazz == Boolean.class) {
      return new BooleanValue().setValue((Boolean) v);
    }
    if (clazz == Long.class) {
      return new IntValue().setValue((Long) v);
    }
    if (clazz == Double.class) {
      return new FloatValue().setValue((Double) v);
    }
    if (clazz == String.class) {
      return new StringValue().setValue((String) v);
    }
    if (v instanceof List) {
      return new ListValue().setValue((List) v);
    }
    if (v instanceof Map) {
      return new ObjectValue().setValue((Map) v);
    }
    throw new GraphLanguageException("Unsupported value type " + clazz);
  }

  static boolean isTypeDefinition(Node node) {
    return node instanceof TypeDefinition;
  }

  static boolean isTypeExtension(Node node) {
    return node instanceof TypeExtension;
  }

  static DefinitionKind getDefinitionKind(Node node) {
    if (node instanceof ScalarTypeDefinition || node instanceof ScalarTypeExtension) {
      return DefinitionKind.SCALAR;
    }
    if (node instanceof InterfaceTypeDefinition || node instanceof InterfaceTypeExtension) {
      return DefinitionKind.INTERFACE;
    }
    if (node instanceof UnionTypeDefinition || node instanceof UnionTypeExtension) {
      return DefinitionKind.UNION;
    }
    if (node instanceof ObjectTypeDefinition || node instanceof ObjectTypeExtension) {
      return DefinitionKind.OBJECT;
    }
    if (node instanceof InputObjectTypeDefinition || node instanceof InputObjectTypeExtension) {
      return DefinitionKind.INPUT;
    }
    if (node instanceof EnumTypeDefinition || node instanceof EnumTypeExtension) {
      return DefinitionKind.ENUM;
    }
    if (node instanceof SchemaDefinition) {
      return DefinitionKind.SCHEMA;
    }
    return DefinitionKind.UNSPECIFIED;
  }
}
