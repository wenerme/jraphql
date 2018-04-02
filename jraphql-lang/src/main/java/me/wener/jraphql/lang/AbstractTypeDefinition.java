package me.wener.jraphql.lang;

import java.util.List;
import lombok.Data;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
@Data
class AbstractTypeDefinition<T> extends AbstractDefinition<T> implements TypeDefinition<T> {

  protected String name;
  protected String description;
  protected List<Directive> directives;

  public T setName(String name) {
    this.name = name;
    return (T) this;
  }

  public T setDescription(String description) {
    this.description = description;
    return (T) this;
  }

  public T setDirectives(List<Directive> directives) {
    this.directives = directives;
    return (T) this;
  }
}
