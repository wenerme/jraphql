package me.wener.jraphql.exec;

import java.util.concurrent.ExecutorService;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/11
 */
public abstract class ExecutableContext {

  public abstract Execution getExecution();

  public TypeSystemDocument getTypeSystemDocument() {
    return getExecution().getTypeSystemDocument();
  }

  public ExecutableDocument getExecutableDocument() {
    return getExecution().getExecutableDocument();
  }

  public void addErrors(Throwable... throwables) {
    getExecution().addErrors(throwables);
  }

  public ExecutorService getExecutorService() {
    return getExecution().getExecutorService();
  }
}
