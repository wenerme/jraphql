package me.wener.jraphql.lang;

import java.util.List;
import java.util.Optional;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
public interface HasDirectives<T> {

  T setDirectives(List<Directive> s);

  List<Directive> getDirectives();

  default Optional<Directive> getDirective(String name) {
    return Langs.findByName(getDirectives(), name);
  }
}
