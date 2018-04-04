package me.wener.jraphql.lang;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
public interface Value extends Node {
  default ValueKind getKind() {
    return Langs.getValueKind(this);
  }

  default Object getValue() {
    throw new UnsupportedOperationException();
  }
}
