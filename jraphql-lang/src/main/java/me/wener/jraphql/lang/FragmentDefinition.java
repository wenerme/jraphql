package me.wener.jraphql.lang;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
@Data
public class FragmentDefinition extends AbstractDefinition<FragmentDefinition>
  implements HasDirectives<FragmentDefinition>,
  HasFragmentName<FragmentDefinition>,
  HasTypeCondition<FragmentDefinition>,
  HasSelectionSet<FragmentDefinition> {

  protected List<Directive> directives;
  @NotNull
  private String fragmentName;
  @NotNull
  private String typeCondition;
  @NotNull
  private SelectionSet selectionSet;
}
