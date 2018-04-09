package me.wener.jraphql.lang;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/4
 */
public enum TypeKind implements Kind {
  NAMED,
  NON_NULL,
  LIST,
  ;

  public boolean isNonnull() {
    return this == NON_NULL;
  }

  public boolean isList() {
    return this == LIST;
  }

  public boolean isNamed() {
    return this == NAMED;
  }
}
