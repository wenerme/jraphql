package me.wener.jraphql.lang;

import lombok.Data;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 30/03/2018
 */
@Data
public class NamedType extends AbstractNode<NamedType> implements Type<NamedType>,
  HasName<NamedType> {

  private String name;

  @Override
  public boolean isNamed() {
    return true;
  }
}
