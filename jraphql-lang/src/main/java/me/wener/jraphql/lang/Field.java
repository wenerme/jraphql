package me.wener.jraphql.lang;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 30/03/2018
 */
@Data
public class Field extends AbstractNode<Field>
    implements Selection<Field>,
        HasName<Field>,
        HasArguments<Field>,
        HasDirectives<Field>,
        HasSelectionSet<Field> {

  private String alias;
  @NotNull private String name;
  private List<Argument> arguments;
  private List<Directive> directives;
  private SelectionSet selectionSet;
}
