package me.wener.jraphql.lang;

import java.util.List;
import lombok.Data;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
@Data
public class AbstractNode<T> implements Node<T> {

  protected SourceLocation sourceLocation;
  protected List<Comment> comments;

  @Override
  public T setSourceLocation(SourceLocation sourceLocation) {
    this.sourceLocation = sourceLocation;
    return (T) this;
  }

  @Override
  public T setComments(List<Comment> comments) {
    this.comments = comments;
    return (T) this;
  }
}
