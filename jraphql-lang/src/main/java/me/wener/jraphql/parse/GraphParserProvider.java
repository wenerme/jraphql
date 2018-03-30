package me.wener.jraphql.parse;

import java.util.ServiceLoader;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 27/03/2018
 */
public interface GraphParserProvider {

  static GraphParserProvider get() {
    ServiceLoader<GraphParserProvider> serviceLoader =
      ServiceLoader.load(GraphParserProvider.class);
    for (GraphParserProvider parser : serviceLoader) {
      return parser;
    }
    throw new GraphParserException("No parser provider");
  }

  GraphParser createParser();
}
