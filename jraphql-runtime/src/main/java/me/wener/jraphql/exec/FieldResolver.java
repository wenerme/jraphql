package me.wener.jraphql.exec;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/4
 */
public interface FieldResolver {
  Object resolve(FieldResolveContext ctx);
}
