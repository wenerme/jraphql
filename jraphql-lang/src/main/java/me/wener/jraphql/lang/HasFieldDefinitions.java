package me.wener.jraphql.lang;

import java.util.List;
import java.util.Optional;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
public interface HasFieldDefinitions<T> {

  T setFieldDefinitions(List<FieldDefinition> fieldDefinitions);

  List<FieldDefinition> getFieldDefinitions();

  default Optional<FieldDefinition> getFieldDefinition(String name) {
    return Langs.findByName(getFieldDefinitions(), name);
  }
}
