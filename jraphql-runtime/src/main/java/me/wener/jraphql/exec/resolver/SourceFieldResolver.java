package me.wener.jraphql.exec.resolver;

import me.wener.jraphql.exec.FieldResolveContext;
import me.wener.jraphql.exec.FieldResolver;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/5/14
 */
public class SourceFieldResolver implements FieldResolver {

  @Override
  public Object resolve(FieldResolveContext ctx) {
    Object source = ctx.getSource();
    if (source instanceof FieldResolver) {
      return ((FieldResolver) source).resolve(ctx);
    }
    return ctx.unresolved();
  }
}
