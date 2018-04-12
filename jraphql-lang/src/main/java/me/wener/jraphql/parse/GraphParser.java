package me.wener.jraphql.parse;

import com.github.wenerme.wava.util.Inputs;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ServiceLoader;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;
import lombok.Value;
import me.wener.jraphql.lang.Document;
import me.wener.jraphql.lang.DocumentDefinition;
import me.wener.jraphql.lang.Langs;
import me.wener.jraphql.parse.GraphParser.ParseOption.ParseOptionBuilder;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 27/03/2018
 */
public interface GraphParser {

  static GraphParser load() {
    ServiceLoader<GraphParser> serviceLoader = ServiceLoader.load(GraphParser.class);
    for (GraphParser parser : serviceLoader) {
      return parser;
    }
    throw new RuntimeException("No parser provider");
  }

  String getProviderName();

  ParseResult parse(ParseOption option);

  default ParseResult parse(BatchParseOption option) {
    ParseOptionBuilder builder = ParseOption.builder();
    List<DocumentDefinition> definitions = Lists.newArrayList();
    for (Entry<String, String> entry : option.getContents().entrySet()) {
      ParseOption opt =
          builder
              .source(entry.getKey())
              .content(entry.getValue())
              .parseType(option.getParseType())
              .hints(option.getHints())
              .build();
      definitions.addAll(parse(opt).getOrThrow().getDefinitions());
    }
    Document merge =
        Document.builder()
            .definitions(Collections.unmodifiableList(definitions))
            .sourceLocation(Langs.runtimeLocation())
            .build();

    return new ParseResult().setDocument(merge);
  }

  default Document parse(String content) {
    return parse(ParseOption.builder().content(content).build()).getOrThrow();
  }

  default Document parseQuery(String content) {
    return parse(ParseOption.builder().parseType(ParseType.EXECUTABLE).content(content).build())
        .getOrThrow();
  }

  default Document parseTypeSystem(String content) {
    return parse(ParseOption.builder().parseType(ParseType.TYPE_SYSTEM).content(content).build())
        .getOrThrow();
  }

  enum ParseType {
    DOCUMENT,
    EXECUTABLE,
    TYPE_SYSTEM
  }

  @Data
  class ParseResult {
    private Document document;
    private RuntimeException exception;

    public Document getOrThrow() {
      if (exception != null) {
        throw exception;
      }
      return document;
    }
  }

  @Value
  @Builder
  class ParseOption {
    @Builder.Default private String source = "inline";
    @NonNull private String content;
    @NonNull @Builder.Default private ParseType parseType = ParseType.DOCUMENT;
    @NonNull @Builder.Default private Map<String, Object> hints = Collections.emptyMap();
  }

  @Value
  @Builder(toBuilder = true)
  class BatchParseOption {
    @NonNull private Map<String, String> contents;
    @NonNull @Builder.Default private ParseType parseType = ParseType.DOCUMENT;
    @NonNull @Builder.Default private Map<String, Object> hints = Collections.emptyMap();

    public static class BatchParseOptionBuilder {
      @Setter(AccessLevel.NONE)
      private Map<String, String> contents = Maps.newHashMap();

      public BatchParseOptionBuilder parseQuery() {
        return parseType(ParseType.EXECUTABLE);
      }

      public BatchParseOptionBuilder parseSchema() {
        return parseType(ParseType.TYPE_SYSTEM);
      }

      public BatchParseOptionBuilder addContent(String source, String content) {
        contents.put(source, content);
        return this;
      }

      public BatchParseOptionBuilder addResourceContent(String name) throws IOException {
        String content = Inputs.getResourceAsString(name);
        if (content == null) {
          throw new IOException("resource not found");
        }
        return addContent(name, content);
      }
    }
  }
}
