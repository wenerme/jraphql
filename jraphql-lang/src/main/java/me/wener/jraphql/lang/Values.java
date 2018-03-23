package me.wener.jraphql.lang;

import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
public interface Values {

  static Value enumOf(String v) {
    return new EnumValue().setValue(v);
  }

  static Value variableOf(String v) {
    return new Variable().setValue(v);
  }

  static Value of(Object v) {
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
    throw new RuntimeException("Unsupported value type " + clazz);
  }

  @Data
  class AbstractValue<T> extends AbstractNode implements Value {

    private T value;
  }

  class IntValue extends AbstractValue<Long> implements Value {}

  class FloatValue extends AbstractValue<Double> implements Value {}

  class BooleanValue extends AbstractValue<Boolean> implements Value {}

  class StringValue extends AbstractValue<String> implements Value {}

  class NullValue extends AbstractValue<Void> implements Value {}

  class EnumValue extends AbstractValue<String> implements Value {}

  class ListValue extends AbstractValue<List<Value>> implements Value {}

  class ObjectValue extends AbstractValue<Map<String, Value>> implements Value {}

  class Variable extends AbstractValue<String> implements Value {}
}
