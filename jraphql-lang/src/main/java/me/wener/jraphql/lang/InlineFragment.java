package me.wener.jraphql.lang;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 30/03/2018
 */
@Data
public class InlineFragment extends AbstractNode<InlineFragment>
    implements Selection<InlineFragment>,
        HasDirectives<InlineFragment>,
        HasSelectionSet<InlineFragment>,
        HasTypeCondition<InlineFragment> {

  private String typeCondition;
  private List<Directive> directives;
  @NotNull private SelectionSet selectionSet;
}
