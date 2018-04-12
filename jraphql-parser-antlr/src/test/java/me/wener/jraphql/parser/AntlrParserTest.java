package me.wener.jraphql.parser;

import static org.junit.Assert.assertEquals;

import com.github.wenerme.wava.util.JSON;
import java.io.IOException;
import me.wener.jraphql.lang.Document;
import me.wener.jraphql.lang.Node;
import me.wener.jraphql.parse.GraphParser;
import me.wener.jraphql.parse.GraphParser.BatchParseOption;
import org.junit.Test;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 30/03/2018
 */
public class AntlrParserTest {

  @Test
  public void testLoader() {
    GraphParser parser = GraphParser.load();
    assert parser != null;
  }

  @Test
  public void testMeta() throws IOException {
    GraphParser parser = GraphParser.load();
    Document document =
        parser
            .parse(BatchParseOption.builder().addResourceContent("meta.graphqls").build())
            .getOrThrow();
    String json = JSON.stringify(document);
    Node node = JSON.parse(json, Node.class);
    assertEquals(document, node);
    assertEquals(json, JSON.stringify(node));
  }

}
