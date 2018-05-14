package me.wener.jraphql.exec.resolver;

import me.wener.jraphql.exec.FieldResolveContext;
import me.wener.jraphql.exec.PostResolver;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/5/14
 */
public class SourcePostResolver implements PostResolver {

  @Override
  public Object postResolve(FieldResolveContext context, Object source) {
    if (source instanceof PostResolver) {
      return ((PostResolver) source).postResolve(context, source);
    }
    return source;
  }
}
