package me.wener.jraphql.exec;

import me.wener.jraphql.lang.OperationDefinition;

/**
 * Top-level of execution context
 *
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/11
 */
public interface ExecuteContext {

  /** @return schema document */
  TypeSystemDocument getTypeSystemDocument();
  /** @return query document */
  ExecutableDocument getExecutableDocument();

  /** @return executed operation */
  OperationDefinition getOperationDefinition();

  /** @return root source of the execution */
  <T> T getSource();
}
