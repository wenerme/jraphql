package me.wener.jraphql.lang;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
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
@JsonDeserialize(builder = Argument.ArgumentBuilder.class)
public class Argument implements Node {

  @NonNull private SourceLocation sourceLocation;
  @NonNull @Builder.Default private List<Comment> comments = Collections.emptyList();

  @NonNull private String name;
  @NonNull private Value value;

  @JsonPOJOBuilder(withPrefix = "")
  public static class ArgumentBuilder
      implements Builders.BuildNode<ArgumentBuilder>,
          Builders.BuildName<ArgumentBuilder>,
          Builders.BuildValue<ArgumentBuilder> {}
}
