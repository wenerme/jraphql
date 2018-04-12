package me.wener.jraphql.lang;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
@JsonDeserialize(builder = StringValue.StringValueBuilder.class)
public class StringValue implements Value {
  @NonNull private SourceLocation sourceLocation;
  @NonNull @Builder.Default private List<Comment> comments = Collections.emptyList();

  @JsonInclude(content = Include.ALWAYS) // may contain empty
  @NonNull
  private String value;

  public static class StringValueBuilder implements Builders.BuildNode<StringValueBuilder> {}
}
