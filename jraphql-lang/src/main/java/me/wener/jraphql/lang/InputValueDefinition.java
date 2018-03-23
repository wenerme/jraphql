package me.wener.jraphql.lang;

import java.util.List;
import lombok.Data;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
@Data
public class InputValueDefinition extends AbstractDefinition
    implements HasName<InputValueDefinition>,
        HasDescription<InputValueDefinition>,
        HasDirectives<InputValueDefinition>,
        HasType<InputValueDefinition>,
        HasDefaultValue<InputValueDefinition> {

  private String name;
  private String description;
  private List<Directive> directives;
  private Type type;
  private Value defaultValue;
}
