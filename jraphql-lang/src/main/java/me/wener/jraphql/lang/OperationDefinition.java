package me.wener.jraphql.lang;

import java.util.List;
import lombok.Data;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 30/03/2018
 */
@Data
public class OperationDefinition extends AbstractDefinition<OperationDefinition>
  implements HasName<OperationDefinition>,
  HasDirectives<OperationDefinition>,
  HasSelectionSet<OperationDefinition> {

  private String operationType;
  private String name;
  private List<VariableDefinition> variableDefinitions;
  private List<Directive> directives;
  private SelectionSet selectionSet;
}
