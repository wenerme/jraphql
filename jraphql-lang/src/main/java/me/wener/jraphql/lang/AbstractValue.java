package me.wener.jraphql.lang;

import lombok.Data;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 30/03/2018
 */
@Data
class AbstractValue<T> extends AbstractNode implements Value {

  private T value;
}
