package me.wener.jraphql.lang;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.NonNull;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 30/03/2018
 */
@lombok.Value
@Builder(toBuilder = true)
@JsonDeserialize(builder = ObjectValue.ObjectValueBuilder.class)
public class ObjectValue implements Value {
  @NonNull private SourceLocation sourceLocation;
  @NonNull @Builder.Default private List<Comment> comments = Collections.emptyList();
  @NonNull @Builder.Default private Map<String, Value> value = Collections.emptyMap();

  @JsonPOJOBuilder(withPrefix = "")
  public static class ObjectValueBuilder implements Builders.BuildNode<ObjectValueBuilder> {}
}
