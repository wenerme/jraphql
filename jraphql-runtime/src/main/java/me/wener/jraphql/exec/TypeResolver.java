package me.wener.jraphql.exec;

import me.wener.jraphql.lang.TypeDefinition;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/12
 */
public interface TypeResolver {

  /**
   * @return {@link String} name of the type, {@link TypeDefinition} the resolved type, {@code null}
   *     failed to resolve
   */
  Object resolveType(FieldResolveContext resolveContext, Object source, String typeName);
}
