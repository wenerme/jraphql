package me.wener.jraphql.parse;

import me.wener.jraphql.lang.SourceLocation;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 30/03/2018
 */
public class GraphParserException extends RuntimeException {

  private final SourceLocation sourceLocation;

  public GraphParserException(String message, SourceLocation sourceLocation) {
    super(message);
    this.sourceLocation = sourceLocation;
  }

  public GraphParserException(String message, Throwable cause, SourceLocation sourceLocation) {
    super(message, cause);
    this.sourceLocation = sourceLocation;
  }
}
