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
@JsonDeserialize(builder = DirectiveDefinition.DirectiveDefinitionBuilder.class)
public class DirectiveDefinition implements TypeDefinition {

  @NonNull private SourceLocation sourceLocation;
  @NonNull @Builder.Default private List<Comment> comments = Collections.emptyList();
  private String description;
  @NonNull @Builder.Default private List<Directive> directives = Collections.emptyList();

  @NonNull private String name;
  @NonNull @Builder.Default private List<String> locations = Collections.emptyList();

  @NonNull @Builder.Default
  private List<InputValueDefinition> argumentDefinitions = Collections.emptyList();

  @JsonPOJOBuilder(withPrefix = "")
  public static class DirectiveDefinitionBuilder
      implements Builders.BuildNode<DirectiveDefinitionBuilder>,
          Builders.BuildName<DirectiveDefinitionBuilder>,
          Builders.BuildDirectives<DirectiveDefinitionBuilder>,
          Builders.BuildArgumentDefinitions<DirectiveDefinitionBuilder> {}
}
