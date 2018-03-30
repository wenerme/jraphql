package me.wener.jraphql.lang;

import java.util.List;
import lombok.Data;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
@Data
public class AbstractTypeExtension<T> extends AbstractDefinition<T> implements TypeExtension<T> {

  protected String name;
  protected String extendTypeName;
  protected List<Directive> directives;

  public T setName(String name) {
    this.name = name;
    return (T) this;
  }

  public T setExtendTypeName(String extendTypeName) {
    this.extendTypeName = extendTypeName;
    return (T) this;
  }

  public T setDirectives(List<Directive> directives) {
    this.directives = directives;
    return (T) this;
  }
}
