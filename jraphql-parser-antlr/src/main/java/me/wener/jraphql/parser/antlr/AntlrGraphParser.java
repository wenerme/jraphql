package me.wener.jraphql.parser.antlr;

import me.wener.jraphql.lang.Document;
import me.wener.jraphql.parse.GraphParser.ParseOption;
import me.wener.jraphql.parser.AntlrParseHint;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DiagnosticErrorListener;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/11
 */
public class AntlrGraphParser {

  private final ParseOption parseOption;
  private final GraphQLLangVisitor visitor;
  private final GraphQLParser parser;
  private final CommonTokenStream stream;

  public AntlrGraphParser(ParseOption parseOption) {
    this.parseOption = parseOption;

    GraphQLLexer lexer = new GraphQLLexer(CharStreams.fromString(parseOption.getContent()));
    stream = new CommonTokenStream(lexer);
    parser = new GraphQLParser(stream);
    parser.addErrorListener(new DiagnosticErrorListener(true));

    AntlrParseHint hint = AntlrParseHint.of(parseOption.getHints());
    parser.setTrimParseTree(hint.getTrimParseTree());
    parser.setBuildParseTree(true);


    visitor = new GraphQLLangVisitor().setSource(parseOption.getSource()).setTokens(stream);
  }

  public Document parse() {
    switch (parseOption.getParseType()) {
      case DOCUMENT:
        return visitor.visitDocument(parser.document());
      case EXECUTABLE:
        return visitor.visitExecutableDocument(parser.executableDocument());
      case TYPE_SYSTEM:
        return visitor.visitTypeSystemDocument(parser.typeSystemDocument());
      default:
        throw new AssertionError();
    }
  }
}
