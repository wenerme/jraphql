package me.wener.jraphql.lang;

import lombok.Value;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
@Value
public class SourceLocation {

  private int line;
  private int column;
}
