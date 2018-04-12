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
@JsonDeserialize(builder = Variable.VariableBuilder.class)
public class Variable implements Value {
  @NonNull private SourceLocation sourceLocation;
  @NonNull @Builder.Default private List<Comment> comments = Collections.emptyList();
  @NonNull private String name;


  public static class VariableBuilder
      implements Builders.BuildNode<VariableBuilder>, Builders.BuildName<VariableBuilder> {}
}
