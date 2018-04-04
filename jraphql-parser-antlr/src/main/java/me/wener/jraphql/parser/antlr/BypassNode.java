package me.wener.jraphql.parser.antlr;

import java.util.List;
import lombok.Data;
import me.wener.jraphql.lang.Comment;
import me.wener.jraphql.lang.SourceLocation;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
@Data
class BypassNode<T> implements me.wener.jraphql.lang.Node {

  private T value;

  public static <T> BypassNode<T> of(T value) {
    return new BypassNode<T>().setValue(value);
  }

  @Override
  public SourceLocation getSourceLocation() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Comment> getComments() {
    throw new UnsupportedOperationException();
  }
}
