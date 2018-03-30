package me.wener.jraphql.lang;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 30/03/2018
 */
public interface HasTypeCondition<T> {

  T setTypeCondition(String typeCondition);

  String getTypeCondition();
}
