package me.wener.jraphql.lang;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
public interface HasExtendTypeName<T> extends HasName<T> {

  T setExtendTypeName(String s);

  String getExtendTypeName();
}
