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
@JsonDeserialize(builder = EnumValueDefinition.EnumValueDefinitionBuilder.class)
public class EnumValueDefinition implements Definition {

  @NonNull private SourceLocation sourceLocation;
  @NonNull @Builder.Default private List<Comment> comments = Collections.emptyList();

  @Builder.Default private String description = "";
  @NonNull private String name; // enumValue
  @NonNull @Builder.Default private List<Directive> directives = Collections.emptyList();

  @JsonPOJOBuilder(withPrefix = "")
  public static class EnumValueDefinitionBuilder
      implements Builders.BuildDefinition<EnumValueDefinitionBuilder>,
          Builders.BuildDirectives<EnumValueDefinitionBuilder>,
          Builders.BuildDescription<EnumValueDefinitionBuilder>,
          Builders.BuildEnumValue<EnumValueDefinitionBuilder> {}
}
