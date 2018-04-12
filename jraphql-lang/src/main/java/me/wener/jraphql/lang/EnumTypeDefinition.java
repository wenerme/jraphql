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
@JsonDeserialize(builder = EnumTypeDefinition.EnumTypeDefinitionBuilder.class)
public class EnumTypeDefinition implements TypeDefinition {

  @NonNull private SourceLocation sourceLocation;
  @NonNull @Builder.Default private List<Comment> comments = Collections.emptyList();
  @NonNull private String name;
  private String description;
  @NonNull @Builder.Default private List<Directive> directives = Collections.emptyList();

  @NonNull @Builder.Default
  private List<EnumValueDefinition> enumValueDefinitions = Collections.emptyList();


  public static class EnumTypeDefinitionBuilder
      implements Builders.BuildTypeDefinition<EnumTypeDefinitionBuilder>,
          Builders.BuildEnumValueDefinitions<EnumTypeDefinitionBuilder> {}
}
