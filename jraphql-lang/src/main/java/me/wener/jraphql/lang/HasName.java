package me.wener.jraphql.lang;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
public interface HasName<T> extends Node<T> {

  T setName(String s);

  String getName();
}
