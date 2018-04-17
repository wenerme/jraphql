package me.wener.jraphql.exec;

import java.util.List;
import lombok.Data;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/17
 */
@Data
class ExecutionError {

  private String message;
  private List<String> path;
  private Throwable exception;
  private ExecuteFieldContext context;

  public ExecuteError toExecuteError() {
    return new ExecuteError().setMessage(message).setPath(path);
  }
}
