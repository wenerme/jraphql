package me.wener.jraphql.lang;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 30/03/2018
 */
@Value
@Builder(toBuilder = true)
@JsonDeserialize(builder = InlineFragment.InlineFragmentBuilder.class)
public class InlineFragment implements Selection {

  @NonNull private SourceLocation sourceLocation;
  @NonNull @Builder.Default private List<Comment> comments = Collections.emptyList();

  private String typeCondition;
  @Builder.Default private List<Directive> directives = Collections.emptyList();
  private SelectionSet selectionSet;


  public static class InlineFragmentBuilder
      implements Builders.BuildNode<InlineFragmentBuilder>,
          Builders.BuildTypeCondition<InlineFragmentBuilder>,
          Builders.BuildDirectives<InlineFragmentBuilder>,
          Builders.BuildSelectionSet<InlineFragmentBuilder> {}
}
