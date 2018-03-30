package me.wener.jraphql.lang;

import java.util.List;
import lombok.Data;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
@Data
public class Directive extends AbstractNode<Directive>
  implements HasName<Directive>, HasArguments<Directive> {

  private String name;
  private List<Argument> arguments;
}
