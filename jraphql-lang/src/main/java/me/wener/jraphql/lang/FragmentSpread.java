package me.wener.jraphql.lang;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 30/03/2018
 */
@Data
public class FragmentSpread extends AbstractNode<FragmentSpread>
  implements Selection<FragmentSpread>, HasDirectives<FragmentSpread> {

  @NotNull
  private String fragmentName;
  private List<Directive> directives;
}
