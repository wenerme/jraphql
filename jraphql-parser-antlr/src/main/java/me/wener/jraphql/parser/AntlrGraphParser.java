package me.wener.jraphql.parser;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map.Entry;
import me.wener.jraphql.lang.Document;
import me.wener.jraphql.lang.DocumentDefinition;
import me.wener.jraphql.parse.GraphParser;
import me.wener.jraphql.parser.antlr.GraphQLLangVisitor;
import me.wener.jraphql.parser.antlr.GraphQLLexer;
import me.wener.jraphql.parser.antlr.GraphQLParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DiagnosticErrorListener;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 30/03/2018
 */
public class AntlrGraphParser implements GraphParser {

  public Document parse(String content) {
    CodePointCharStream input = CharStreams.fromString(content);
    GraphQLParser parser = buildParser(input);
    return new GraphQLLangVisitor().visitDocument(parser.document());
  }

  @Override
  public Document parse(Option option) {

    List<DocumentDefinition> definitions = Lists.newArrayList();
    GraphQLLangVisitor visitor = new GraphQLLangVisitor();
    for (Entry<String, String> entry : option.getSourceMap().entrySet()) {
      GraphQLLexer lexer = new GraphQLLexer(CharStreams.fromString(entry.getValue()));
      CommonTokenStream stream = new CommonTokenStream(lexer);
      GraphQLParser parser = new GraphQLParser(stream);
      parser.addErrorListener(new DiagnosticErrorListener(true));
      parser.setBuildParseTree(true);

      visitor.setSource(entry.getKey());
      switch (option.getParseType()) {
        case DOCUMENT:
          definitions.addAll(visitor.visitDocument(parser.document()).getDefinitions());
          break;
        case EXECUTABLE:
          definitions.addAll(
              visitor.visitExecutableDocument(parser.executableDocument()).getDefinitions());
          break;
        case TYPE_SYSTEM:
          definitions.addAll(
              visitor.visitTypeSystemDocument(parser.typeSystemDocument()).getDefinitions());
          break;
        default:
          throw new AssertionError();
      }
    }

    return Document.builder().definitions(definitions).build();
  }

  public Document parseQuery(String content) {
    CodePointCharStream input = CharStreams.fromString(content);
    GraphQLParser parser = buildParser(input);
    return new GraphQLLangVisitor().visitExecutableDocument(parser.executableDocument());
  }

  public Document parseTypeSystem(String content) {
    CodePointCharStream input = CharStreams.fromString(content);
    GraphQLParser parser = buildParser(input);
    return new GraphQLLangVisitor().visitTypeSystemDocument(parser.typeSystemDocument());
  }

  private GraphQLParser buildParser(CodePointCharStream input) {
    GraphQLLexer lexer = new GraphQLLexer(input);
    CommonTokenStream stream = new CommonTokenStream(lexer);
    GraphQLParser parser = new GraphQLParser(stream);
    parser.addErrorListener(new DiagnosticErrorListener(true));
    parser.setBuildParseTree(true);
    return parser;
  }
}
