package me.wener.jraphql.lang;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
public interface Type extends Node {

  default boolean isNonNull() {
    return this instanceof NonNullType;
  }

  default boolean isList() {
    return this instanceof ListType;
  }

  default boolean isNamed() {
    return this instanceof NamedType;
  }

  default Type getType() {
    return null;
  }

  default String getName() {
    return null;
  }
}
