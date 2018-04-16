package me.wener.jraphql.exec;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/13
 */
public interface FieldResolverRegistry {

  /** Always use the same field resolver */
  static FieldResolverRegistry identity(FieldResolver fieldResolver) {
    return (context) -> fieldResolver;
  }

  FieldResolver lookup(FieldResolveContext context);
}
