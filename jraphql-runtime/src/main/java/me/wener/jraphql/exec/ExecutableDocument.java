package me.wener.jraphql.exec;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import me.wener.jraphql.lang.Document;
import me.wener.jraphql.lang.DocumentDefinition;
import me.wener.jraphql.lang.ExecutableDefinition;
import me.wener.jraphql.lang.FragmentDefinition;
import me.wener.jraphql.lang.GraphLanguageException;
import me.wener.jraphql.lang.OperationDefinition;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/4
 */
@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder(toBuilder = true)
public class ExecutableDocument {
  @NonNull private Map<String, ExecutableDefinition> definitions;
  @NonNull private TypeSystemDocument typeSystemDocument;

  @NonNull private Map<String, OperationDefinition> operationDefinitions;
  @NonNull private Map<String, FragmentDefinition> fragmentDefinitions;
  private OperationDefinition defaultOperation;

  public static ExecutableDocumentBuilder builder() {
    return new ExecutableDocumentBuilderPrebuild() {
      @Override
      public ExecutableDocument build() {
        prebuild();
        return super.build();
      }
    };
  }

  public OperationDefinition getOperationDefinition(String name) {
    return operationDefinitions.get(name);
  }

  public FragmentDefinition getFragmentDefinition(String name) {
    return fragmentDefinitions.get(name);
  }

  public FragmentDefinition requireFragmentDefinition(String name) {
    //    fragmentDefinitions
    FragmentDefinition fragmentDefinition = fragmentDefinitions.get(name);
    if (fragmentDefinition == null) {
      throw new GraphExecuteException(String.format("FragmentDefinition %s not found", name));
    }
    return fragmentDefinition;
  }

  private static class ExecutableDocumentBuilderPrebuild extends ExecutableDocumentBuilder {

    void prebuild() {
      definitions = Maps.newHashMap();
      ImmutableMap.Builder<String, FragmentDefinition> fragmentDefinitionBuilder =
          ImmutableMap.builder();
      ImmutableMap.Builder<String, OperationDefinition> operationDefinitionBuilder =
          ImmutableMap.builder();

      for (DocumentDefinition definition : documentDefinitions) {
        if (definition instanceof OperationDefinition) {
          OperationDefinition d = definition.unwrap(OperationDefinition.class);
          if (d.getName() != null) {
            operationDefinitionBuilder.put(d.getName(), d);
          }
          if (definition.getName() == null) {
            if (defaultOperation == null) {
              defaultOperation = (OperationDefinition) definition;
              continue; // SKIP
            } else {
              throw new GraphLanguageException("Multi default operation found");
            }
          }
        } else if (definition instanceof FragmentDefinition) {
          FragmentDefinition d = definition.unwrap(FragmentDefinition.class);
          fragmentDefinitionBuilder.put(d.getName(), d);
        } else {
          throw new GraphLanguageException("Unexpected definition in executable document");
        }

        if (definitions.put(definition.getName(), (ExecutableDefinition) definition) != null) {
          throw new GraphLanguageException(
              "Duplicated definition for name: " + definition.getName());
        }
      }

      definitions = ImmutableMap.copyOf(definitions);
      operationDefinitions = operationDefinitionBuilder.build();
      fragmentDefinitions = fragmentDefinitionBuilder.build();

      if (defaultOperation == null) {
        if (operationDefinitions.size() == 1) {
          defaultOperation = operationDefinitions.values().iterator().next();
        }
      }
    }
  }

  @Accessors(chain = true, fluent = true)
  public static class ExecutableDocumentBuilder {
    protected final List<DocumentDefinition> documentDefinitions = Lists.newArrayList();

    @Setter(AccessLevel.NONE)
    protected Map<String, ExecutableDefinition> definitions;

    @Setter(AccessLevel.NONE)
    protected Map<String, OperationDefinition> operationDefinitions;

    @Setter(AccessLevel.NONE)
    protected Map<String, FragmentDefinition> fragmentDefinitions;

    @Setter(AccessLevel.NONE)
    protected OperationDefinition defaultOperation;

    public ExecutableDocumentBuilder addDocuments(Document... documents) {
      for (Document document : documents) {
        documentDefinitions.addAll(document.getDefinitions());
      }
      return this;
    }
  }
}
