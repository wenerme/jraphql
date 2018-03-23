package me.wener.jraphql.lang;

import java.util.List;
import lombok.Data;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
@Data
public class AbstractNode implements Node {

  protected SourceLocation sourceLocation;
  protected List<Comment> comments;
  //  protected List<Node> children;
}
