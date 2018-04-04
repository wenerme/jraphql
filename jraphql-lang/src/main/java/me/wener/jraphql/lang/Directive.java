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
@JsonDeserialize(builder = Directive.DirectiveBuilder.class)
public class Directive implements Node {

  @NonNull private SourceLocation sourceLocation;
  @NonNull @Builder.Default private List<Comment> comments = Collections.emptyList();

  @NonNull private String name;
  @NonNull @Builder.Default private List<Argument> arguments = Collections.emptyList();

  @JsonPOJOBuilder(withPrefix = "")
  public static class DirectiveBuilder
      implements Builders.BuildNode<DirectiveBuilder>,
          Builders.BuildName<DirectiveBuilder>,
          Builders.BuildArguments<DirectiveBuilder> {}
}
