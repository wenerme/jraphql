package me.wener.jraphql.lang;

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
@JsonDeserialize(builder = EnumValue.EnumValueBuilder.class)
public class EnumValue implements Value {
  @NonNull private SourceLocation sourceLocation;
  @NonNull @Builder.Default private List<Comment> comments = Collections.emptyList();
  @NonNull private String name;

  @Override
  public Object getValue() {
    // FIXME Ensure how to handle the value of enum
    return name;
  }

  public static class EnumValueBuilder implements Builders.BuildNode<EnumValueBuilder> {}
}
