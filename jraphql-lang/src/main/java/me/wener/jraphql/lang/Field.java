package me.wener.jraphql.lang;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
@JsonDeserialize(builder = Field.FieldBuilder.class)
public class Field implements Selection {

  @NonNull private SourceLocation sourceLocation;
  @NonNull @Builder.Default private List<Comment> comments = Collections.emptyList();

  private String alias;
  @NonNull private String name;
  @NonNull @Builder.Default private List<Argument> arguments = Collections.emptyList();
  @NonNull @Builder.Default private List<Directive> directives = Collections.emptyList();
  private SelectionSet selectionSet;


  public static class FieldBuilder
      implements Builders.BuildNode<FieldBuilder>,
          Builders.BuildName<FieldBuilder>,
          Builders.BuildArguments<FieldBuilder>,
          Builders.BuildDirectives<FieldBuilder>,
          Builders.BuildSelectionSet<FieldBuilder> {}
}
