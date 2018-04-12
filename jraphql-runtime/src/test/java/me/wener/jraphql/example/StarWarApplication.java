package me.wener.jraphql.example;

import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.method;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wenerme.wava.util.JSON;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.wener.jraphql.example.StarWarResolverV1.StarWarData;
import me.wener.jraphql.exec.DefaultGraphExecutor;
import me.wener.jraphql.exec.ExecuteResult;
import me.wener.jraphql.exec.MetaResolver;
import me.wener.jraphql.exec.TableExecuteResolver;
import me.wener.jraphql.exec.TypeSystemDocument;
import me.wener.jraphql.parse.GraphParser;
import me.wener.jraphql.parse.GraphParser.BatchParseOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/10
 */
@SpringBootApplication
@Slf4j
public class StarWarApplication {
  public static void main(String[] args) {
    SpringApplication.run(StarWarApplication.class, args);
  }

  @Bean
  public RouterFunction<ServerResponse> gqlRouterFunction() throws IOException {
    GraphParser parser = GraphParser.load();
    MetaResolver metaResolver = new MetaResolver();
    StarWarResolverV1 starWarResolver = new StarWarResolverV1();
    TableExecuteResolver resolver =
        TableExecuteResolver.builder()
            .forType(
                starWarResolver,
                "Query",
                "Starship",
                "Character",
                "Human",
                "Droid",
                "FriendsEdge",
                "FriendsConnection",
                "PageInfo",
                "Mutation",
                "Review",
                "ReviewInput")
            .forMeta(metaResolver)
            .fallback(metaResolver)
            .build();
    StarWarData source = StarWarResolverV1.loadData();
    DefaultGraphExecutor executor =
        DefaultGraphExecutor.builder()
            .parser(parser)
            .resolver(resolver)
            .typeResolver(starWarResolver)
            .typeSystemDocument(
                TypeSystemDocument.builder()
                    //                    .addMetaDocument()
                    .addDocument(
                        parser
                            .parse(
                                BatchParseOption.builder()
                                    .parseSchema()
                                    .addResourceContent("meta.graphqls")
                                    .addResourceContent("starwars.graphqls")
                                    .build())
                            .getOrThrow())
                    .build())
            .build();

    ObjectMapper mapper =
        new ObjectMapper()
            .findAndRegisterModules()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return route(
        path("/query").and(method(HttpMethod.POST).or(method(HttpMethod.GET))),
        request -> {
          Mono<QueryRequest> requestMono =
              request.bodyToMono(String.class).map(v -> JSON.parse(v, QueryRequest.class));
          return ok().contentType(MediaType.APPLICATION_JSON_UTF8)
              .body(
                  requestMono
                      .flatMap(
                          r -> {
                            log.info("QUERY {}", r);
                            CompletionStage<ExecuteResult> result =
                                executor.execute(
                                    r.getQuery(), r.getOperationName(), r.getVariables(), source);
                            return Mono.fromCompletionStage(result);
                          })
                      .map(
                          v -> {
                            try {
                              return mapper.writeValueAsString(v);
                            } catch (JsonProcessingException e) {
                              throw new RuntimeException(e);
                            }
                          }),
                  String.class);
        });
  }

  @Bean
  public RouterFunction<ServerResponse> graphiqlRouterFunction(
      @Value("classpath:/static/graphiql.html") Resource index) {
    return route(GET("/"), request -> ok().contentType(TEXT_HTML).syncBody(index));
  }

  @Data
  public static class QueryRequest {
    private String query;
    private String operationName;
    private Map<String, Object> variables;
  }
}
