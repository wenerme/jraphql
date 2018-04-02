package me.wener.jraphql.lang;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
public interface Type<T> extends Node<T> {

  default boolean isNonNull() {
    return false;
  }

  default boolean isList() {
    return false;
  }

  default boolean isNamed() {
    return false;
  }

  default Type getType() {
    return null;
  }

  /** @return Deepest type name */
  default String resolveTypeName() {
    if (this.isNamed()) {
      return this.getName();
    } else {
      return this.getType().resolveTypeName();
    }
  }
}
