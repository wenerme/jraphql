package me.wener.jraphql.lang;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
public interface TypeDefinition<T>
  extends Definition<T>, HasName<T>, HasDescription<T>, HasDirectives<T> {

}
