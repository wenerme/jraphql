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
@JsonDeserialize(builder = UnionTypeDefinition.UnionTypeDefinitionBuilder.class)
public class UnionTypeDefinition implements TypeDefinition {

  @NonNull private SourceLocation sourceLocation;
  @NonNull @Builder.Default private List<Comment> comments = Collections.emptyList();
  @NonNull private String name;
  private String description;
  @NonNull @Builder.Default private List<Directive> directives = Collections.emptyList();
  @NonNull @Builder.Default private List<String> memberTypes = Collections.emptyList();


  public static class UnionTypeDefinitionBuilder
      implements Builders.BuildTypeDefinition<UnionTypeDefinitionBuilder> {}
}
