package me.wener.jraphql.lang;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
public interface Type extends Node {

  /** Nested type of the nonnull or list */
  default Type getType() {
    return null;
  }

  /** type name of the named type */
  @Nullable
  @Override
  default String getName() {
    return null;
  }

  default TypeKind getKind() {
    return Langs.getTypeKind(this);
  }

  /** @return Deepest type name */
  @Nonnull
  default String resolveTypeName() {
    if (this.getKind().isNamed()) {
      return this.getName();
    } else {
      return this.getType().resolveTypeName();
    }
  }
}
