package me.wener.jraphql.lang;

import lombok.Data;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 30/03/2018
 */
@Data
public class VariableDefinition extends AbstractDefinition<VariableDefinition>
  implements HasType<VariableDefinition>, HasDefaultValue<VariableDefinition> {

  private String variableName;
  private Type type;
  private Value defaultValue;
}
