package me.wener.jraphql.lang;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 30/03/2018
 */
@lombok.Value
@Builder(toBuilder = true)
@JsonDeserialize(builder = VariableDefinition.VariableDefinitionBuilder.class)
public class VariableDefinition implements Definition {
  @NonNull private SourceLocation sourceLocation;
  @NonNull @Builder.Default private List<Comment> comments = Collections.emptyList();

  private String name;
  private Type type;
  private Value defaultValue;


  public static class VariableDefinitionBuilder
      implements Builders.BuildDefinition<VariableDefinitionBuilder>,
          Builders.BuildType<VariableDefinitionBuilder>,
          Builders.BuildDefaultValue<VariableDefinitionBuilder> {}
}
