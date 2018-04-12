package me.wener.jraphql.lang;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
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
@JsonDeserialize(builder = ListValue.ListValueBuilder.class)
public class ListValue implements Value {
  @NonNull private SourceLocation sourceLocation;
  @NonNull @Builder.Default private List<Comment> comments = Collections.emptyList();
  @NonNull @Builder.Default private List<Value> value = Collections.emptyList();


  public static class ListValueBuilder implements Builders.BuildNode<ListValueBuilder> {}
}
