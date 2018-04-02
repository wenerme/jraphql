package me.wener.jraphql.lang;

import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 30/03/2018
 */
@Data
public class ListType extends AbstractNode<ListType> implements Type<ListType>, HasType<ListType> {

  @NotNull private Type type;

  @Override
  public boolean isList() {
    return true;
  }
}
