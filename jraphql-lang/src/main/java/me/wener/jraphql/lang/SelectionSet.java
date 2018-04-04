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
@JsonDeserialize(builder = SelectionSet.SelectionSetBuilder.class)
public class SelectionSet implements Node {
  @NonNull private SourceLocation sourceLocation;
  @NonNull @Builder.Default private List<Comment> comments = Collections.emptyList();
  @NonNull @Builder.Default private List<Selection> selections = Collections.emptyList();

  @JsonPOJOBuilder(withPrefix = "")
  public static class SelectionSetBuilder implements Builders.BuildNode<SelectionSetBuilder> {}
}
