package me.wener.jraphql.lang;

import lombok.Data;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
@Data
public class BypassNode<T> extends AbstractIntermediaNode {

  private T value;

  public static <T> BypassNode<T> of(T value) {
    return new BypassNode<T>().setValue(value);
  }
}
