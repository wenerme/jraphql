package me.wener.jraphql.exec;

/**
 * Resolve a field to a source
 *
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/4
 */
public interface FieldResolver {

  /** @return a resolver always resolved to unresolved */
  static FieldResolver unresolved() {
    return FieldResolveContext::unresolved;
  }

  /** @return a resolver use the given registry to lookup resolver and resolve */
  static FieldResolver registry(FieldResolverRegistry registry) {
    return (ctx) -> registry.lookup(ctx).resolve(ctx);
  }

  /**
   * Resolve a field, can accept
   *
   * <ul>
   *   <li>CompletionStage for async
   *   <li>any value will become the next resolve source
   *   <li>{@code null} no more resolve
   * </ul>
   */
  Object resolve(FieldResolveContext ctx);
}
