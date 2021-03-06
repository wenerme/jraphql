package me.wener.jraphql.lang;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import me.wener.jraphql.lang.Builders.BuildFieldDefinitions;
import me.wener.jraphql.lang.Builders.BuildTypeExtension;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
@Value
@Builder(toBuilder = true)
@JsonDeserialize(builder = InterfaceTypeExtension.InterfaceTypeExtensionBuilder.class)
public class InterfaceTypeExtension implements TypeExtension {

  @NonNull private SourceLocation sourceLocation;
  @NonNull @Builder.Default private List<Comment> comments = Collections.emptyList();
  @NonNull @Builder.Default private List<Directive> directives = Collections.emptyList();
  @NonNull private String extendTypeName;
  private String extendByName;
  private String name;

  @Builder.Default private List<FieldDefinition> fieldDefinitions = Collections.emptyList();


  public static class InterfaceTypeExtensionBuilder
      implements BuildTypeExtension<InterfaceTypeExtensionBuilder>,
          BuildFieldDefinitions<InterfaceTypeExtensionBuilder> {}
}
