package me.wener.jraphql.parse;

import me.wener.jraphql.lang.Document;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 30/03/2018
 */
public interface GraphParser {

  Document parse(String content);

  Document parseQuery(String content);

  Document parseTypeSystem(String content);
}
