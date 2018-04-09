package me.wener.jraphql.lang;

import java.util.List;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 30/03/2018
 */
public interface Selection extends Node {
  List<Directive> getDirectives();
}
