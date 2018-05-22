package me.wener.jraphql.exec;

import com.github.wenerme.wava.util.Later;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import me.wener.jraphql.generator.GraphQLGenerator;
import me.wener.jraphql.lang.Field;
import me.wener.jraphql.lang.GraphLanguageException;
import me.wener.jraphql.lang.ObjectTypeDefinition;
import me.wener.jraphql.lang.OperationDefinition;
import me.wener.jraphql.lang.SelectionSet;
import me.wener.jraphql.lang.Type;
import me.wener.jraphql.lang.TypeDefinition;
import me.wener.jraphql.lang.TypeDefinitionKind;
import me.wener.jraphql.lang.VariableDefinition;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/11
 */
@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder(toBuilder = true)
@Slf4j
public class Execution implements ExecuteContext {

  @NonNull private String id;

  @NonNull private TypeSystemDocument typeSystemDocument;
  @NonNull private ExecutableDocument executableDocument;

  private String operationName;
  @NonNull private String operationType;
  @NonNull private OperationDefinition operationDefinition;
  @NonNull private ObjectTypeDefinition operationTypeDefinition;
  @NonNull private FieldResolver fieldResolver;
  @NonNull private PostResolver postResolver;
  @NonNull private TypeResolver typeResolver;
  @NotNull private Introspection.Schema introspectionSchema;

  private List<ExecutionError> errors;
  private CompletableFuture<Object> result = new CompletableFuture<>();

  @NonNull private Map<String, Object> variables;
  private Object source;

  private ExecutorService executorService;

  public static ExecutionBuilder builder() {
    return new ExecutionBuilderPrebuild();
  }

  protected CompletionStage<Object> extractFieldValue(
      ExecuteFieldContext context, Type fieldType, Object source) {
    log.trace("Extract Field {} {}", GraphQLGenerator.generateType(fieldType), source);

    try {
      return _extractFieldValue(context, fieldType, source);
    } catch (Throwable e) {
      log.warn("Failed Extract Field {} {}", GraphQLGenerator.generateType(fieldType), source, e);
      return Later.completeExceptionally(e);
    }
  }

  private CompletionStage<Object> _extractFieldValue(
      ExecuteFieldContext context, Type fieldType, Object source) {

    switch (fieldType.getKind()) {
      case NAMED:
        if (source == null) {
          return CompletableFuture.completedFuture(null);
        }
        TypeDefinitionKind kind =
            context
                .getExecution()
                .getTypeSystemDocument()
                .requireDefinition(fieldType.getName())
                .getKind();
        if (kind == TypeDefinitionKind.SCALAR || kind == TypeDefinitionKind.ENUM) {
          return CompletableFuture.completedFuture(source);
        }

        ExecuteTypeContext typeContext =
            context.createFieldTypeContext(source, fieldType.getName());
        executeType(typeContext);
        return typeContext.getValue();
      case NON_NULL:
        return extractFieldValue(context, fieldType.getType(), source)
            .thenApplyAsync(
                v -> {
                  if (v == null) {
                    throw new GraphExecuteException("null for nonnull");
                  }
                  return v;
                });
      case LIST:
        if (source == null) {
          return CompletableFuture.completedFuture(null);
        } else if (!(source instanceof Iterable)) {
          return Later.completeExceptionally(
              new GraphExecuteException("list need iterable result"));
        } else {
          // Expand
          ArrayList<CompletableFuture<Object>> items = Lists.newArrayList();
          for (Object o : ((Iterable) source)) {
            items.add(extractFieldValue(context, fieldType.getType(), o).toCompletableFuture());
          }
          return CompletableFuture.allOf(items.toArray(new CompletableFuture<?>[items.size()]))
              .thenApply(
                  __ -> {
                    // Handle exception
                    return items.stream().map(i -> i.getNow(null)).collect(Collectors.toList());
                  });
        }
    }
    throw new AssertionError();
  }

  protected void executeType(ExecuteTypeContext context) {
    log.trace("Execute Type {}", context.getObjectTypeDefinition().getName());

    try {
      _executeType(context);
    } catch (Exception e) {
      log.warn("Failed Execute Type {}", context.getObjectTypeDefinition().getName(), e);
      context.getValue().completeExceptionally(e);
    }
  }

  protected CompletionStage<?> doResolveField(ExecuteFieldContext context) {
    try {
      CompletionStage<Object> resolved =
          CompletableFuture.supplyAsync(() -> fieldResolver.resolve(context), getExecutorService());
      return resolved
          .thenCompose(v -> Later.asCompletionStage(v).toCompletableFuture())
          .thenCompose(
              v -> {
                if (v != null) {
                  return Later.asCompletionStage(postResolver.postResolve(context, v));
                }
                return CompletableFuture.completedFuture(null);
              })
          .thenApply(
              v -> {
                if (context.isUnresolved(v)) {
                  throw new GraphExecuteException(
                      String.format(
                          "unresolved field `%s.%s`",
                          context.getObjectName(), context.getFieldName()));
                }
                return v;
              });
    } catch (Exception e) {
      log.warn("Failed to ResolveField `{}`.`{}`", context.getObjectName(), context.getField(), e);
      return Later.completeExceptionally(e);
    }
  }

  //  protected FieldResolver lookupFieldResolver(ExecuteFieldContext context) {
  //    if (context.getSource() instanceof FieldResolver) {
  //      return (FieldResolver) context.getSource();
  //    }
  //    return resolverRegistry.lookup(context);
  //  }

  protected void _executeType(ExecuteTypeContext context) {
    for (Field field : context.getFieldSelection().values()) {
      ExecuteFieldContext fieldContext = context.createFieldContext(field);
      CompletionStage<?> source = doResolveField(fieldContext);

      source.whenComplete(
          (v, e) -> {
            CompletableFuture<Object> value = fieldContext.getValue();
            if (e != null) {
              value.completeExceptionally(e);
            } else if (v == null) {
              value.complete(null);
            } else {
              extractFieldValue(fieldContext, fieldContext.getFieldType(), v)
                  .whenCompleteAsync(Later.complete(value));
            }
          });
    }
    context.aggregateValue();
  }

  public Execution addError(ExecuteFieldContext context, Throwable e) {
    ExecutionError error = new ExecutionError();
    error
        .setContext(context)
        .setException(e)
        .setMessage(e.getMessage())
        .setPath(context.collectPath());
    return addError(error);
  }

  public Execution addError(ExecutionError err) {
    errors.add(err);
    return this;
  }

  ExecuteTypeContext createTypeContext(
      ObjectTypeDefinition objectTypeDefinition, SelectionSet selectionSet, Object source) {

    return ExecuteTypeContext.builder()
        .execution(this)
        .source(source)
        .objectTypeDefinition(objectTypeDefinition)
        .selectionSet(selectionSet)
        .build();
  }

  void execute() {
    ExecuteTypeContext context =
        createTypeContext(operationTypeDefinition, operationDefinition.getSelectionSet(), source);

    executeType(context);
    context.getValue().whenComplete(Later.complete(result));
  }

  public TypeDefinition resolveType(ExecuteFieldContext context, Object source, String name) {
    TypeResolver resolver = typeResolver;
    TypeDefinition targetType = null;
    if (source instanceof TypeResolver) {
      resolver = (TypeResolver) source;
    }

    if (resolver == null) {
      throw Errors.typeResolveFailed(name);
    }

    Object target = resolver.resolveType(context, source);
    if (target == null) {
      throw Errors.typeResolveFailed(name);
    }

    if (target instanceof String) {
      targetType = getTypeSystemDocument().requireDefinition((String) target);
    } else if (target instanceof TypeDefinition) {
      targetType = (TypeDefinition) target;
    } else {
      // Incorrect
      throw Errors.typeResolveFailed(name);
    }

    return targetType;
  }

  @Accessors(fluent = true, chain = true)
  public static class ExecutionBuilder {

    // This is thread safe, will access by async
    @Setter(AccessLevel.NONE)
    protected List<ExecutionError> errors = Lists.newCopyOnWriteArrayList();

    @Setter(AccessLevel.PRIVATE)
    protected String operationType;

    public ExecutionBuilder addError(ExecutionError err) {
      errors.add(err);
      return this;
    }
  }

  private static class ExecutionBuilderPrebuild extends ExecutionBuilder {

    @Override
    public Execution build() {
      prebuild();
      return super.build();
    }

    private void prebuild() {
      try {
        _prebuild();
      } catch (Exception e) {
        log.warn("Execution prebuild failed", e);
        this.addError(
            new ExecutionError()
                .setMessage(e.getMessage())
                .setException(e)
                .setPath(Collections.emptyList()));
      }
    }

    private void _prebuild() {

      if (super.executorService == null) {
        super.executorService = ForkJoinPool.commonPool();
      }

      if (super.id == null) {
        super.id = UUID.randomUUID().toString();
      }
      if (super.operationDefinition == null) {
        if (Strings.isNullOrEmpty(super.operationName)) {
          super.operationDefinition = super.executableDocument.getDefaultOperation();
          if (super.operationDefinition == null) {
            throw new GraphLanguageException("default operation not found");
          }
          // track the operation name
          super.operationName = super.operationDefinition.getName();
        } else {
          super.operationDefinition =
              super.executableDocument.getOperationDefinition(super.operationName);
          if (super.operationDefinition == null) {
            throw new GraphLanguageException(
                String.format("operation '%s' not found", super.operationName));
          }
        }
      }

      operationType = super.operationDefinition.getOperationType();
      super.operationTypeDefinition =
          super.typeSystemDocument.getOperationTypeDefinition(operationType);

      if (super.variables == null) {
        super.variables = Collections.emptyMap();
      }

      for (VariableDefinition definition : super.operationDefinition.getVariableDefinitions()) {
        String name = definition.getName();
        Type type = definition.getType();
        Object value = super.variables.get(name);
        if (value == null) {
          if (definition.getDefaultValue() == null) {
            // TODO should i use null for missing nullable variable
            throw new GraphLanguageException(String.format("variable '%s' not found", name));
          }

          super.variables.put(name, value = definition.getDefaultValue().getName());
        }

        // TODO Handle error
        TypeSystemDocument.checkValueType(type, value);
      }
    }
  }
}
