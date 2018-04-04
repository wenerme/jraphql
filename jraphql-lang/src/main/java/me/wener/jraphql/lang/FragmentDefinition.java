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
 * @since 16/03/2018
 */
@Value
@Builder(toBuilder = true)
@JsonDeserialize(builder = FragmentDefinition.FragmentDefinitionBuilder.class)
public class FragmentDefinition implements Definition {

  @NonNull private SourceLocation sourceLocation;
  @NonNull @Builder.Default private List<Comment> comments = Collections.emptyList();
  @NonNull private String name;
  @NonNull private String typeCondition;
  private SelectionSet selectionSet;
  @NonNull @Builder.Default private List<Directive> directives = Collections.emptyList();

  @JsonPOJOBuilder(withPrefix = "")
  public static class FragmentDefinitionBuilder
      implements Builders.BuildDefinition<FragmentDefinitionBuilder>,
          Builders.BuildName<FragmentDefinitionBuilder>,
          Builders.BuildDirectives<FragmentDefinitionBuilder>,
          Builders.BuildTypeCondition<FragmentDefinitionBuilder>,
          Builders.BuildSelectionSet<FragmentDefinitionBuilder> {}
}
