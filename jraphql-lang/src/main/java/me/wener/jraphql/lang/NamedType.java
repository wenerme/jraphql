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
@JsonDeserialize(builder = NamedType.NamedTypeBuilder.class)
public class NamedType implements Type {

  @NonNull private SourceLocation sourceLocation;
  @NonNull @Builder.Default private List<Comment> comments = Collections.emptyList();
  @NonNull private String name;

  @JsonPOJOBuilder(withPrefix = "")
  public static class NamedTypeBuilder
      implements Builders.BuildNode<NamedTypeBuilder>, Builders.BuildName<NamedTypeBuilder> {}
}
