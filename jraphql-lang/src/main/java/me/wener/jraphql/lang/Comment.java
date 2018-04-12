package me.wener.jraphql.lang;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import lombok.Builder;
import lombok.NonNull;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
@lombok.Value
@Builder(toBuilder = true)
@JsonDeserialize(builder = Comment.CommentBuilder.class)
public class Comment implements Node {
  @NonNull private SourceLocation sourceLocation;

  @JsonInclude(content = Include.ALWAYS) // may contain empty
  @NonNull
  private String content;

  @Nonnull
  @Override
  public List<Comment> getComments() {
    return Collections.emptyList();
  }

  public static class CommentBuilder implements Builders.Builder {}
}
