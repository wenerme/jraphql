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
@JsonDeserialize(builder = FragmentSpread.FragmentSpreadBuilder.class)
public class FragmentSpread implements Selection {

  @NonNull private SourceLocation sourceLocation;
  @NonNull @Builder.Default private List<Comment> comments = Collections.emptyList();

  @NonNull private String fragmentName;
  @NonNull @Builder.Default private List<Directive> directives = Collections.emptyList();


  public static class FragmentSpreadBuilder
      implements Builders.BuildNode<FragmentSpreadBuilder>,
          Builders.BuildDirectives<FragmentSpreadBuilder> {}
}
