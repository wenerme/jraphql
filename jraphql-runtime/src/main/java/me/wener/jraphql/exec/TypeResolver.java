package me.wener.jraphql.exec;

import me.wener.jraphql.lang.ObjectTypeDefinition;
import me.wener.jraphql.lang.TypeDefinition;

/**
 * Resolve a union or interface
 *
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/12
 */
public interface TypeResolver {

  /**
   * Resolve to an {@linkplain ObjectTypeDefinition Object} type, can accept
   *
   * <ul>
   *   <li>{@link String} name of the type
   *   <li>{@link TypeDefinition} the resolved type
   *   <li>{@code null} failed to resolve
   * </ul>
   */
  Object resolveType(FieldResolveContext resolveContext, Object source, String typeName);

  /** @return Return a new TypeResolver that will try next resolver. */
  default TypeResolver then(TypeResolver next) {
    return (resolveContext, source, typeName) -> {
      Object first = this.resolveType(resolveContext, source, typeName);
      if (first == null) {
        return next.resolveType(resolveContext, source, typeName);
      }
      return first;
    };
  }
}
