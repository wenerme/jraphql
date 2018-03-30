package me.wener.jraphql.parser;

import com.github.wenerme.wava.util.JSON;
import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import me.wener.jraphql.lang.Document;
import me.wener.jraphql.parse.GraphParser;
import me.wener.jraphql.parse.GraphParserProvider;
import org.junit.Test;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 30/03/2018
 */
public class AntlrParserTest {

  @Test
  public void testLoader() {
    GraphParser parser = GraphParserProvider.get().createParser();
    assert parser != null;
  }

  @Test
  public void testParseMeta() throws IOException {
    AntlrGraphParser parser = new AntlrGraphParser();
    InputStream stream = ClassLoader.getSystemResourceAsStream("introspection-query.graphql");
    assert stream != null;
    String s = CharStreams.toString(new InputStreamReader(stream));
    Document document = parser.parse(s);
    System.out.println(JSON.stringify(document, true));
  }
}
