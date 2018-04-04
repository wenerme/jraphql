package me.wener.jraphql.parse;

import java.util.Map;
import lombok.Data;
import me.wener.jraphql.lang.Document;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 30/03/2018
 */
public interface GraphParser {

  Document parse(String content);

  Document parse(Option option);

  Document parseQuery(String content);

  Document parseTypeSystem(String content);

  enum ParseType {
    DOCUMENT,
    EXECUTABLE,
    TYPE_SYSTEM
  }

  @Data
  class Option {
    private Map<String, String> sourceMap;
    private ParseType parseType = ParseType.DOCUMENT;
  }
}
