package me.wener.jraphql.exec;

/**
 * Resolve a field to a source
 *
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/4
 */
@FunctionalInterface
public interface FieldResolver extends ExecutionResolver {

  /** @return a resolver always resolved to unresolved */
  static FieldResolver unresolved() {
    return FieldResolveContext::unresolved;
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
