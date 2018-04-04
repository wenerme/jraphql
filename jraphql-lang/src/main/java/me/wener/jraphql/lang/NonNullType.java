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
@JsonDeserialize(builder = NonNullType.NonNullTypeBuilder.class)
public class NonNullType implements Type {

  @NonNull private SourceLocation sourceLocation;
  @NonNull @Builder.Default private List<Comment> comments = Collections.emptyList();
  @NonNull private Type type;

  @JsonPOJOBuilder(withPrefix = "")
  public static class NonNullTypeBuilder implements Builders.BuildNode<NonNullTypeBuilder> {}
}
