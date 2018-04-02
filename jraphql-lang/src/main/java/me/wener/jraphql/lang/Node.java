package me.wener.jraphql.lang;

import java.util.List;
import javax.annotation.Nullable;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
public interface Node<T> {

  SourceLocation getSourceLocation();

  T setSourceLocation(SourceLocation sourceLocation);

  List<Comment> getComments();

  T setComments(List<Comment> comments);

  /**
   * Node may contain a name
   *
   * @return name of the node
   */
  @Nullable
  default String getName() {
    return null;
  }

  default String getNodeTypeName() {
    return getClass().getSimpleName();
  }
}
