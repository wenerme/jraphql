package me.wener.jraphql.exec;

import java.util.Map;
import java.util.concurrent.CompletionStage;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 27/03/2018
 */
public interface GraphExecutor {

  /**
   * Execute the query
   */
  CompletionStage<Response> execute(
    ExecuteContext execute, String query, String operationName, Map<String, Object> variables);

  void validate(String query);
}
