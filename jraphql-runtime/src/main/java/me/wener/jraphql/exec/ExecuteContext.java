package me.wener.jraphql.exec;

import me.wener.jraphql.lang.OperationDefinition;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/11
 */
public interface ExecuteContext {

  TypeSystemDocument getTypeSystemDocument();

  ExecutableDocument getExecutableDocument();

  OperationDefinition getOperationDefinition();

  /** @return root source of the execution */
  <T> T getSource();
}
