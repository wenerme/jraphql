package me.wener.jraphql.lang;

import lombok.Data;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
@Data
public class Argument extends AbstractNode implements HasName<Argument>, HasValue<Argument> {

  private String name;
  private Value value;
}
