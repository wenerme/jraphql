package me.wener.jraphql.lang;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import me.wener.jraphql.lang.Builders.BuildTypeExtension;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
@Value
@Builder(toBuilder = true)
@JsonDeserialize(builder = UnionTypeExtension.UnionTypeExtensionBuilder.class)
public class UnionTypeExtension implements TypeExtension {
  @NonNull private SourceLocation sourceLocation;
  @NonNull @Builder.Default private List<Comment> comments = Collections.emptyList();
  @NonNull @Builder.Default private List<Directive> directives = Collections.emptyList();
  @NonNull private String extendTypeName;
  private String name;
  @NonNull @Builder.Default private List<String> unionMemberTypes = Collections.emptyList();


  public static class UnionTypeExtensionBuilder
      implements BuildTypeExtension<UnionTypeExtensionBuilder> {}
}
