package me.wener.jraphql.exec;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.Wither;
import me.wener.jraphql.lang.Field;
import me.wener.jraphql.lang.FieldDefinition;
import me.wener.jraphql.lang.ObjectTypeDefinition;
import me.wener.jraphql.lang.Type;
import me.wener.jraphql.lang.TypeDefinition;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/11
 */
@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder(toBuilder = true)
class ExecuteFieldContext extends ExecutableContext implements FieldResolveContext {

  private final CompletableFuture<Object> value = new CompletableFuture<>();
  private Execution execution;
  private String fieldName;
  private Field field;
  @Wither private Type fieldType;
  private FieldDefinition fieldDefinition;
  private Map<String, Object> arguments;
  private ExecuteTypeContext parent;
  private Object source;

  ExecuteTypeContext createFieldTypeContext(Object source, String typeName) {
    TypeDefinition definition = getTypeSystemDocument().requireDefinition(typeName);
    //      fieldTypeContext = new ExecuteTypeContext();
    switch (definition.getKind()) {
      case INTERFACE:
      case UNION:
        definition = execution.resolveType(this, source, typeName);
    }

    ExecuteTypeContext typeContext =
        ExecuteTypeContext.builder()
            .execution(execution)
            .parentFieldContext(this)
            .source(source)
            .objectTypeDefinition(definition.unwrap(ObjectTypeDefinition.class))
            .selectionSet(field.getSelectionSet())
            .build();
    return typeContext;
  }

  @Override
  public ExecuteContext getExecuteContext() {
    return execution;
  }

  public String getAliasOrName() {
    if (field.getAlias() != null) {
      return field.getAlias();
    }
    return field.getName();
  }

  @Override
  public ObjectTypeDefinition getObjectTypeDefinition() {
    return parent.getObjectTypeDefinition();
  }

  public static class ExecuteFieldContextBuilder {}
}
