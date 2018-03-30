package me.wener.jraphql.lang;

import java.util.List;
import java.util.Optional;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
public interface HasArguments<T> {

  T setArguments(List<Argument> arguments);

  List<Argument> getArguments();

  default Optional<Argument> getArgument(String name) {
    return Langs.findByName(getArguments(), name);
  }
}
