package me.wener.jraphql.exec;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 27/03/2018
 */
public class GraphExecuteException extends RuntimeException {

  public GraphExecuteException() {
    super();
  }

  public GraphExecuteException(String message) {
    super(message);
  }

  public GraphExecuteException(String message, Throwable cause) {
    super(message, cause);
  }

  public GraphExecuteException(Throwable cause) {
    super(cause);
  }
}
