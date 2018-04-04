package me.wener.jraphql.lang;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 29/03/2018
 */
public interface Langs {

  static <T extends Node> Optional<T> findByName(Iterable<T> list, String name) {
    if (list == null) {
      return Optional.empty();
    }
    for (T t : list) {
      if (Objects.equals(t.getName(), name)) {
        return Optional.of(t);
      }
    }
    return Optional.empty();
  }

  static boolean isTypeDefinition(Node node) {
    return node instanceof TypeDefinition;
  }

  static boolean isTypeExtension(Node node) {
    return node instanceof TypeExtension;
  }

  static ValueKind getValueKind(Node node) {
    return Holder.valueKindMap.get(node.getClass());
  }

  static TypeKind getTypeKind(Node node) {
    return Holder.typeKindMap.get(node.getClass());
  }

  static TypeDefinitionKind getTypeDefinitionKind(Node node) {
    return Holder.typeDefinitionKindMap.get(node.getClass());
  }

  static Object resolveValue(Value value) {
    return resolveValue(value, Collections.emptyMap());
  }

  static Object resolveValue(Value value, Map<String, Object> variables) {
    switch (value.getKind()) {
      case NULL:
      case FLOAT:
      case INT:
      case STRING:
      case BOOLEAN:
        return value.getValue();
      case ENUM:
        return value.unwrap(EnumValue.class).getName();
      case OBJECT:
        {
          ObjectValue map = value.unwrap(ObjectValue.class);
          HashMap<String, Object> values = Maps.newHashMapWithExpectedSize(map.getValue().size());
          map.getValue().forEach((k, v) -> values.put(k, resolveValue(v, variables)));
          return values;
        }
      case LIST:
        {
          ListValue list = value.unwrap(ListValue.class);
          ArrayList<Object> values = Lists.newArrayListWithCapacity(list.getValue().size());
          for (Value v : list.getValue()) {
            values.add(resolveValue(v, variables));
          }
          return values;
        }
      case VARIABLE:
        Variable variable = value.unwrap(Variable.class);
        return variables.get(variable.getName());
      default:
        throw new AssertionError();
    }
  }

  final class Holder {
    private static final ImmutableMap<Class<?>, TypeDefinitionKind> typeDefinitionKindMap;
    private static final ImmutableMap<Class<?>, ValueKind> valueKindMap;
    private static final ImmutableMap<Class<?>, TypeKind> typeKindMap;

    static {
      {
        Builder<Class<?>, TypeDefinitionKind> builder = ImmutableMap.builder();
        builder.put(SchemaDefinition.class, TypeDefinitionKind.SCHEMA);
        builder.put(EnumTypeDefinition.class, TypeDefinitionKind.ENUM);
        builder.put(EnumTypeExtension.class, TypeDefinitionKind.ENUM);
        builder.put(InputObjectTypeDefinition.class, TypeDefinitionKind.INPUT);
        builder.put(InputObjectTypeExtension.class, TypeDefinitionKind.INPUT);
        builder.put(ObjectTypeDefinition.class, TypeDefinitionKind.OBJECT);
        builder.put(ObjectTypeExtension.class, TypeDefinitionKind.OBJECT);
        builder.put(UnionTypeDefinition.class, TypeDefinitionKind.UNION);
        builder.put(UnionTypeExtension.class, TypeDefinitionKind.UNION);
        builder.put(InterfaceTypeDefinition.class, TypeDefinitionKind.INTERFACE);
        builder.put(InterfaceTypeExtension.class, TypeDefinitionKind.INTERFACE);
        builder.put(ScalarTypeDefinition.class, TypeDefinitionKind.SCALAR);
        builder.put(ScalarTypeExtension.class, TypeDefinitionKind.SCALAR);
        typeDefinitionKindMap = builder.build();
      }
      {
        Builder<Class<?>, ValueKind> builder = ImmutableMap.builder();
        builder.put(NullValue.class, ValueKind.NULL);
        builder.put(FloatValue.class, ValueKind.FLOAT);
        builder.put(ObjectValue.class, ValueKind.OBJECT);
        builder.put(ListValue.class, ValueKind.LIST);
        builder.put(IntValue.class, ValueKind.INT);
        builder.put(EnumValue.class, ValueKind.ENUM);
        builder.put(StringValue.class, ValueKind.STRING);
        builder.put(BooleanValue.class, ValueKind.BOOLEAN);
        builder.put(Variable.class, ValueKind.VARIABLE);
        valueKindMap = builder.build();
      }
      {
        Builder<Class<?>, TypeKind> builder = ImmutableMap.builder();
        builder.put(NonNullType.class, TypeKind.NONNULL);
        builder.put(ListType.class, TypeKind.LIST);
        builder.put(NamedType.class, TypeKind.NAMED);
        typeKindMap = builder.build();
      }
    }
  }
}
