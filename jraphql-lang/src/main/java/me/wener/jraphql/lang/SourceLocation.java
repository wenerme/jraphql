package me.wener.jraphql.lang;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
@Value
@Builder(toBuilder = true)
@JsonDeserialize(builder = SourceLocation.SourceLocationBuilder.class)
public class SourceLocation {

  private int line;
  private int column;
  @NonNull private String source;

  @JsonPOJOBuilder(withPrefix = "")
  public static class SourceLocationBuilder {}
}
