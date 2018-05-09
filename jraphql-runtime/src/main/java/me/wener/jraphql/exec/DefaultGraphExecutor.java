package me.wener.jraphql.exec;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import me.wener.jraphql.lang.Document;
import me.wener.jraphql.parse.GraphParser;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 30/03/2018
 */
@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder(toBuilder = true)
@Slf4j
public class DefaultGraphExecutor implements GraphExecutor {

  @NonNull private GraphParser parser;
  @NonNull private ExecutorService executorService;
  @NonNull private TypeSystemDocument typeSystemDocument;
  @NonNull private FieldResolverRegistry resolverRegistry;
  @NonNull @Builder.Default private TypeResolver typeResolver = (ctx, s, n) -> null;

  @Override
  public CompletionStage<ExecuteResult> execute(
      String query, String operationName, Map<String, Object> variables, Object source) {

    if (variables == null) {
      variables = Maps.newHashMap();
    } else {
      variables = Maps.newHashMap(variables);
    }

    Document document = parser.parseQuery(query);
    ExecutableDocument executableDocument =
        ExecutableDocument.builder()
            .addDocuments(document)
            .typeSystemDocument(typeSystemDocument)
            .build();

    Execution execution =
        Execution.builder()
            .typeSystemDocument(typeSystemDocument)
            .executableDocument(executableDocument)
            .operationName(operationName)
            .variables(variables)
            .source(source)
            .executorService(executorService)
            .resolverRegistry(resolverRegistry)
            .typeResolver(typeResolver)
            .build();

    CompletableFuture.runAsync(execution::execute, executorService);

    return execution
        .getResult()
        .exceptionally(
            e -> {
              // SHOULD NOT HAPPEN
              log.warn("Failed to execute", e);
              return null;
            })
        .thenApply(
            v -> {
              ExecuteResult result = new ExecuteResult();
              result.setErrors(
                  execution
                      .getErrors()
                      .stream()
                      .map(ExecutionError::toExecuteError)
                      .collect(Collectors.toList()));
              if (result.getErrors().isEmpty()) {
                result.setData(v);
              }
              return result;
            });
  }

  public static class DefaultGraphExecutorBuilder {
    private ExecutorService executorService = ForkJoinPool.commonPool();

    public DefaultGraphExecutorBuilder directExecutorService() {
      executorService = MoreExecutors.newDirectExecutorService();
      return this;
    }

    public DefaultGraphExecutorBuilder resolver(FieldResolver resolver) {
      resolverRegistry = FieldResolverRegistry.identity(resolver);
      return this;
    }
  }
}
