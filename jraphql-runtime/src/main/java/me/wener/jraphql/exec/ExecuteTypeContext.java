package me.wener.jraphql.exec;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import me.wener.jraphql.lang.Argument;
import me.wener.jraphql.lang.Directive;
import me.wener.jraphql.lang.Field;
import me.wener.jraphql.lang.FieldDefinition;
import me.wener.jraphql.lang.FragmentDefinition;
import me.wener.jraphql.lang.FragmentSpread;
import me.wener.jraphql.lang.InlineFragment;
import me.wener.jraphql.lang.InputValueDefinition;
import me.wener.jraphql.lang.Langs;
import me.wener.jraphql.lang.ObjectTypeDefinition;
import me.wener.jraphql.lang.Selection;
import me.wener.jraphql.lang.SelectionSet;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/11
 */
@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder(toBuilder = true)
public class ExecuteTypeContext extends ExecutableContext {

  private Object source;
  private Execution execution;
  private ObjectTypeDefinition objectTypeDefinition;
  private SelectionSet selectionSet;
  private Map<String, ExecuteFieldContext> fields = Maps.newLinkedHashMap();
  private ExecuteFieldContext parentFieldContext;
  private CompletableFuture<Object> value = new CompletableFuture<>();

  private List<Selection> skippedSelection;
  private List<Selection> mismatchSelection;
  private Map<String, Field> fieldSelection;

  public static ExecuteTypeContextBuilder builder() {
    return new ExecuteTypeContextBuilderPrebuild();
  }

  ExecuteFieldContext createFieldContext(Field field) {
    FieldDefinition fieldDefinition =
        getTypeSystemDocument()
            .requireFieldDefinition(objectTypeDefinition.getName(), field.getName());

    HashMap<String, Object> arguments = Maps.newHashMap();
    for (Argument argument : field.getArguments()) {
      arguments.put(
          argument.getName(), Langs.resolveValue(argument.getValue(), execution.getVariables()));
    }
    // set default argument and check value
    for (InputValueDefinition definition : fieldDefinition.getArgumentDefinitions()) {
      String name = definition.getName();
      if (!arguments.containsKey(name)) {
        if (definition.getDefaultValue() != null) {
          arguments.put(name, Langs.resolveValue(definition.getDefaultValue()));
        }
      }
      TypeSystemDocument.checkValueType(definition.getType(), arguments.get(name));
    }

    ExecuteFieldContext context =
        ExecuteFieldContext.builder()
            .execution(execution)
            .parent(this)
            .fieldName(field.getName())
            .field(field)
            .source(source)
            .fieldType(fieldDefinition.getType())
            .fieldDefinition(fieldDefinition)
            .arguments(arguments)
            .build();

    fields.put(context.getAliasOrName(), context);
    return context;
  }

  public void aggregateValue() {
    CompletableFuture<?>[] futures =
        fields
            .values()
            .stream()
            .map(ExecuteFieldContext::getValue)
            .toArray(CompletableFuture[]::new);
    CompletableFuture.allOf(futures)
        .whenCompleteAsync(
            (v, e) -> {
              if (e != null) {
                value.completeExceptionally(e);
              } else {
                // match the order of selection
                Map<String, Object> map = Maps.newLinkedHashMap();
                for (ExecuteFieldContext context : fields.values()) {
                  map.put(
                      context.getAliasOrName(),
                      context.getValue().toCompletableFuture().getNow(null));
                }
                value.complete(map);
              }
            });
  }

  public static class ExecuteTypeContextBuilder {

    protected List<Selection> skippedSelection;
    protected List<Selection> mismatchSelection;
    protected Map<String, Field> fieldSelection;
  }

  protected static class ExecuteTypeContextBuilderPrebuild extends ExecuteTypeContextBuilder {

    @Override
    public ExecuteTypeContext build() {
      prebuild();
      return super.build();
    }

    protected void collectSelectionSet(SelectionSet selectionSet) {
      for (Selection selection : selectionSet.getSelections()) {
        if (isSkipped(selection)) {
          skippedSelection.add(selection);
          continue;
        }
        collectSelection(selection);
      }
    }

    protected boolean isTypeMatch(String typeCondition) {
      if (typeCondition != null) {
        // TODO possible type check
        if (!super.objectTypeDefinition.getName().equals(typeCondition)) {
          return false;
        }
      }
      return true;
    }

    protected void collectSelection(Selection selection) {

      if (selection instanceof InlineFragment) {
        InlineFragment fragment = selection.unwrap(InlineFragment.class);

        if (isTypeMatch(fragment.getTypeCondition())) {
          collectSelectionSet(fragment.getSelectionSet());
        } else {
          mismatchSelection.add(selection);
        }

      } else if (selection instanceof FragmentSpread) {
        FragmentSpread fragment = selection.unwrap(FragmentSpread.class);
        FragmentDefinition fragmentDefinition =
            super.execution
                .getExecutableDocument()
                .requireFragmentDefinition(fragment.getFragmentName());

        if (isTypeMatch(fragmentDefinition.getTypeCondition())) {
          collectSelectionSet(fragmentDefinition.getSelectionSet());
        } else {
          mismatchSelection.add(selection);
        }
      } else if (selection instanceof Field) {
        Field field = selection.unwrap(Field.class);
        Field old = fieldSelection.put(selection.getName(), field);
        if (old != null) {
          // replace old field
          FieldDefinition fieldDefinition =
              super.execution
                  .getTypeSystemDocument()
                  .requireFieldDefinition(super.objectTypeDefinition.getName(), field.getName());

          switch (super.execution
              .getTypeSystemDocument()
              .requireDefinition(fieldDefinition.getType().resolveTypeName())
              .getKind()) {
            case OBJECT:
              // TODO Deep merge
          }
        }
      }
    }

    protected boolean isSkipped(Selection selection) {
      boolean include = true;
      for (Directive directive : selection.getDirectives()) {
        switch (directive.getName()) {
          case "skip":
          case "include":
            me.wener.jraphql.lang.Value cond =
                Langs.findByName(directive.getArguments(), "if")
                    .map(Argument::getValue)
                    .orElse(Langs.runtimeNullValue());
            Object v = Langs.resolveValue(cond);
            if (v == null) {
              throw new GraphExecuteException("null for nonnull");
            }
            if (!(v instanceof Boolean)) {
              throw new GraphExecuteException("expected boolean");
            }
            include = Boolean.TRUE.equals(v);
            if (directive.getName().equals("skip")) {
              // invert
              include = !include;
            }
        }
      }
      return !include;
    }

    protected void prebuild() {
      // collect all fields
      skippedSelection = Lists.newArrayList();
      mismatchSelection = Lists.newArrayList();
      fieldSelection = Maps.newLinkedHashMap();
      collectSelectionSet(super.selectionSet);
    }
  }
}
