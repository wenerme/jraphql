package me.wener.jraphql.runtime;

import java.util.concurrent.CompletionStage;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 23/03/2018
 */
public interface PostResolve {

  /** Hook for the resolver has been created */
  CompletionStage<Void> postResolve();
}
