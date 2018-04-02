package me.wener.jraphql.lang;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
public interface TypeExtension<T> extends Definition<T>, HasExtendTypeName<T>, HasDirectives<T> {}
