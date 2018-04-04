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
@JsonDeserialize(builder = FieldDefinition.FieldDefinitionBuilder.class)
public class FieldDefinition implements Definition {

  @NonNull private SourceLocation sourceLocation;
  @NonNull @Builder.Default private List<Comment> comments = Collections.emptyList();

  private String description;
  @NonNull private String name;

  @NonNull @Builder.Default
  private List<InputValueDefinition> argumentDefinitions = Collections.emptyList();

  @NonNull private Type type;
  @NonNull @Builder.Default private List<Directive> directives = Collections.emptyList();

  @JsonPOJOBuilder(withPrefix = "")
  public static class FieldDefinitionBuilder
      implements Builders.BuildDefinition<FieldDefinitionBuilder>,
          Builders.BuildName<FieldDefinitionBuilder>,
          Builders.BuildDescription<FieldDefinitionBuilder>,
          Builders.BuildArgumentDefinitions<FieldDefinitionBuilder>,
          Builders.BuildDirectives<FieldDefinitionBuilder>,
          Builders.BuildType<FieldDefinitionBuilder> {}
}
