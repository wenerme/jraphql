package me.wener.jraphql.lang;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import me.wener.jraphql.lang.Builders.BuildFieldDefinitions;
import me.wener.jraphql.lang.Builders.BuildInterfaces;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
@Value
@Builder(toBuilder = true)
@JsonDeserialize(builder = ObjectTypeDefinition.ObjectTypeDefinitionBuilder.class)
public class ObjectTypeDefinition implements TypeDefinition {

  @NonNull private SourceLocation sourceLocation;
  @NonNull @Builder.Default private List<Comment> comments = Collections.emptyList();
  @NonNull private String name;
  private String description;
  @NonNull @Builder.Default private List<Directive> directives = Collections.emptyList();

  @NonNull @Builder.Default private List<String> interfaces = Collections.emptyList();

  @NonNull @Builder.Default
  private List<FieldDefinition> fieldDefinitions = Collections.emptyList();

  public static ObjectTypeDefinitionBuilder builder() {
    return new ObjectTypeDefinitionBuilder() {
      @Override
      public ObjectTypeDefinition build() {
        prebuild();
        return super.build();
      }
    };
  }

  public static class ObjectTypeDefinitionBuilder
      implements Builders.BuildTypeDefinition<ObjectTypeDefinitionBuilder>,
          BuildInterfaces<ObjectTypeDefinitionBuilder>,
          BuildFieldDefinitions<ObjectTypeDefinitionBuilder> {
    protected void prebuild() {
      interfaces = ImmutableSet.copyOf(interfaces).asList();
    }
  }
}
