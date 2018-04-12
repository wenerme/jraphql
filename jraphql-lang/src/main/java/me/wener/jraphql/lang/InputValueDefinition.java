package me.wener.jraphql.lang;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
@lombok.Value
@Builder(toBuilder = true)
@JsonDeserialize(builder = InputValueDefinition.InputValueDefinitionBuilder.class)
public class InputValueDefinition implements Definition {

  @NonNull private SourceLocation sourceLocation;
  @NonNull @Builder.Default private List<Comment> comments = Collections.emptyList();

  @NonNull private String name;
  private String description;
  @NonNull @Builder.Default private List<Directive> directives = Collections.emptyList();
  @NonNull private Type type;
  private Value defaultValue;

  public static class InputValueDefinitionBuilder
      implements Builders.BuildDefinition<InputValueDefinitionBuilder>,
          Builders.BuildDescription<InputValueDefinitionBuilder>,
          Builders.BuildName<InputValueDefinitionBuilder>,
          Builders.BuildDirectives<InputValueDefinitionBuilder>,
          Builders.BuildType<InputValueDefinitionBuilder>,
          Builders.BuildDefaultValue<InputValueDefinitionBuilder> {}
}
