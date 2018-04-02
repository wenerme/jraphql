package me.wener.jraphql.lang;

import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 30/03/2018
 */
@Data
public class NonNullType extends AbstractNode<ListType>
    implements Type<ListType>, HasType<NonNullType> {

  @NotNull private Type type;

  @Override
  public boolean isNonNull() {
    return true;
  }
}
