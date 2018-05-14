package me.wener.jraphql.exec;

import java.util.concurrent.CompletionStage;

/**
 * Post-process for one resolve
 *
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 23/03/2018
 */
@FunctionalInterface
public interface PostResolver extends ExecutionResolver {

  /** @return a PostResolver always return source in parameter */
  static PostResolver identity() {
    return (c, s) -> s;
  }

  /**
   * @param context field context
   * @param source last resolved result. the source is synchronized, not incomplete value
   * @return new source or the original. may return incomplete value like {@link CompletionStage}
   */
  Object postResolve(FieldResolveContext context, Object source);
}
