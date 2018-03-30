package me.wener.jraphql.parser;

import me.wener.jraphql.lang.Document;
import me.wener.jraphql.parse.GraphParser;
import me.wener.jraphql.parser.antlr.GraphQLLangVisitor;
import me.wener.jraphql.parser.antlr.GraphQLLexer;
import me.wener.jraphql.parser.antlr.GraphQLParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DiagnosticErrorListener;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 30/03/2018
 */
public class AntlrGraphParser implements GraphParser {

  public Document parse(String content) {
    GraphQLLexer lexer = new GraphQLLexer(CharStreams.fromString(content));
    CommonTokenStream stream = new CommonTokenStream(lexer);
    GraphQLParser parser = new GraphQLParser(stream);
    parser.addErrorListener(new DiagnosticErrorListener(true));
    parser.setBuildParseTree(true);
    return new GraphQLLangVisitor().visitDocument(parser.document());
  }
}
