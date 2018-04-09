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
@JsonDeserialize(builder = OperationDefinition.OperationDefinitionBuilder.class)
public class OperationDefinition implements ExecutableDefinition {

  @NonNull private SourceLocation sourceLocation;
  @NonNull @Builder.Default private List<Comment> comments = Collections.emptyList();

  @NonNull private String operationType;
  private String name;

  @NonNull @Builder.Default
  private List<VariableDefinition> variableDefinitions = Collections.emptyList();

  @NonNull @Builder.Default private List<Directive> directives = Collections.emptyList();
  private SelectionSet selectionSet;

  @JsonPOJOBuilder(withPrefix = "")
  public static class OperationDefinitionBuilder
      implements Builders.BuildDefinition<OperationDefinitionBuilder>,
          Builders.BuildName<OperationDefinitionBuilder>,
          Builders.BuildDirectives<OperationDefinitionBuilder>,
          Builders.BuildSelectionSet<OperationDefinitionBuilder> {}
}
