package me.wener.jraphql.parser;

import me.wener.jraphql.lang.Document;
import me.wener.jraphql.parse.GraphParser;
import me.wener.jraphql.parser.antlr.AntlrGraphParser;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 22/03/2018
 */
public class AntlrGraphParserProvider implements GraphParser {

  @Override
  public String getProviderName() {
    return "antlr";
  }

  @Override
  public ParseResult parse(ParseOption option) {
    AntlrGraphParser parser = new AntlrGraphParser(option);
    Document document = parser.parse();
    ParseResult result = new ParseResult();
    result.setDocument(document);
    return result;
  }
}
