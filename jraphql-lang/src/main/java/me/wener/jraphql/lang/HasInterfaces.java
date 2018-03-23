package me.wener.jraphql.lang;

import java.util.List;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
public interface HasInterfaces<T> {

  T setInterfaces(List<String> interfaces);
}
