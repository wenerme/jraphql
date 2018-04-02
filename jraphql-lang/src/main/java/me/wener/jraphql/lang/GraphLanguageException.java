package me.wener.jraphql.lang;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 30/03/2018
 */
public class GraphLanguageException extends RuntimeException {

  public GraphLanguageException() {}

  public GraphLanguageException(String message) {
    super(message);
  }

  public GraphLanguageException(String message, Throwable cause) {
    super(message, cause);
  }

  public GraphLanguageException(Throwable cause) {
    super(cause);
  }
}
