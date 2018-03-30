package me.wener.jraphql.lang;

import java.util.List;
import java.util.Optional;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
public interface HasArgumentDefinitions<T> {

  T setArgumentDefinitions(List<InputValueDefinition> s);

  List<InputValueDefinition> getArgumentDefinitions();

  default Optional<InputValueDefinition> getArgumentDefinition(String name) {
    return Langs.findByName(getArgumentDefinitions(), name);
  }
}
