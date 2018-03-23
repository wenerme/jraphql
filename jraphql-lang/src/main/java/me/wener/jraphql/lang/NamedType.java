package me.wener.jraphql.lang;

import lombok.Data;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
@Data
public class NamedType extends AbstractNode implements Type, HasName<NamedType> {

  private String name;
}
