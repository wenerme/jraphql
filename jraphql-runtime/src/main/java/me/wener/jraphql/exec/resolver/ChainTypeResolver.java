package me.wener.jraphql.exec.resolver;

import java.util.List;
import lombok.RequiredArgsConstructor;
import me.wener.jraphql.exec.FieldResolveContext;
import me.wener.jraphql.exec.TypeResolver;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/5/14
 */
@RequiredArgsConstructor
public class ChainTypeResolver implements TypeResolver {
  private final List<TypeResolver> resolvers;

  @Override
  public Object resolveType(FieldResolveContext resolveContext, Object source) {
    for (TypeResolver resolver : resolvers) {
      Object resolved = resolver.resolveType(resolveContext, source);
      if (resolved != null) {
        return resolved;
      }
    }
    return null;
  }
}
