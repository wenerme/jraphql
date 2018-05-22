package me.wener.jraphql.exec;

import java.util.Map;
import me.wener.jraphql.lang.Field;
import me.wener.jraphql.lang.FieldDefinition;
import me.wener.jraphql.lang.ObjectTypeDefinition;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/11
 */
public interface FieldResolveContext {

  ExecuteContext getExecuteContext();

  Field getField();

  FieldDefinition getFieldDefinition();

  ObjectTypeDefinition getObjectTypeDefinition();

  default String getFieldName() {
    return getField().getName();
  }

  default String getObjectName() {
    return getObjectTypeDefinition().getName();
  }

  <T> T getSource();

  default <T> T getRootSource() {
    return getExecuteContext().getSource();
  }

  Map<String, Object> getArguments();

  /** @return a value represent unresolved, how to handle the unresolved is decided by execution */
  default Object unresolved() {
    return AlternativeValue.UNRESOLVED;
  }

  default boolean isUnresolved(Object source) {
    return AlternativeValue.UNRESOLVED == source;
  }

  <T> T get(Object key);

  <T> T put(Object key, T val);

  <T> T remove(Object key);
}
