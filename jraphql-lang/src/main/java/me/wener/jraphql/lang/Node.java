package me.wener.jraphql.lang;

import java.util.Collections;
import java.util.List;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
public interface Node {

  SourceLocation getSourceLocation();

  List<Comment> getComments();

  default List<Node> getChildren() {
    return Collections.emptyList();
  }

  default String getTypeName() {
    return getClass().getSimpleName();
  }
}
