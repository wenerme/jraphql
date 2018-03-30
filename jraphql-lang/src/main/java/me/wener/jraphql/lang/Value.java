package me.wener.jraphql.lang;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
public interface Value extends Node {

  default boolean isVariable() {
    return this instanceof Variable;
  }

  <T> T getValue();
}
