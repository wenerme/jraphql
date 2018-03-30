package me.wener.jraphql.parse;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 30/03/2018
 */
public class GraphParserException extends RuntimeException {

  public GraphParserException() {
  }

  public GraphParserException(String message) {
    super(message);
  }

  public GraphParserException(String message, Throwable cause) {
    super(message, cause);
  }

  public GraphParserException(Throwable cause) {
    super(cause);
  }
}
