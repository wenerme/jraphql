package me.wener.jraphql.parser;

import me.wener.jraphql.parse.GraphParser;
import me.wener.jraphql.parse.GraphParserProvider;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 22/03/2018
 */
public class AntlrGraphParserProvider implements GraphParserProvider {

  @Override
  public GraphParser createParser() {
    return new AntlrGraphParser();
  }
}
