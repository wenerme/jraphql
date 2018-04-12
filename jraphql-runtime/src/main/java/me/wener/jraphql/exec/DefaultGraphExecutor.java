package me.wener.jraphql.exec;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
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
@Builder
@Slf4j
public class DefaultGraphExecutor implements GraphExecutor {

  private GraphParser parser;
  private ExecutorService executorService = MoreExecutors.newDirectExecutorService();
  private TypeSystemDocument typeSystemDocument;
  private FieldResolver resolver;
  private TypeResolver typeResolver;

  private Table<String, String, FieldResolver> resolvers = HashBasedTable.create();

  public static BiConsumer<Object, Throwable> complete(CompletableFuture<Object> value) {
    return (v, e) -> {
      if (e != null) {
        value.completeExceptionally(e);
      } else {
        value.complete(v);
      }
    };
  }

  public static <T> CompletionStage<T> completeExceptionally(Throwable e) {
    CompletableFuture<T> future = new CompletableFuture<>();
    future.completeExceptionally(e);
    return future;
  }

  public static CompletionStage<Object> asAsync(Object o) {
    if (o instanceof CompletionStage) {
      return (CompletionStage<Object>) o;
    }
    return CompletableFuture.completedFuture(o);
  }

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
            .resolver(resolver)
            .typeResolver(typeResolver)
            .build();

    CompletableFuture.runAsync(execution::execute, executorService);

    return execution
        .getResult()
        .thenApplyAsync(
            v -> {
              ExecuteResult result = new ExecuteResult();
              result.setData(v);
              result.setErrors(execution.getErrors());
              return result;
            });
  }

  @Override
  public void validate(String query) {}
}
