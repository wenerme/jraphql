package me.wener.jraphql.example;

import static org.junit.Assert.assertEquals;

import com.github.wenerme.wava.util.JSON;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import java.util.regex.Pattern;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;
import me.wener.jraphql.lang.Document;
import me.wener.jraphql.parse.GraphParser;
import me.wener.jraphql.parse.GraphParser.BatchParseOption;
import me.wener.jraphql.schema.MetaSchema;
import org.junit.Test;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/12
 */
public class EmbededSchema {

  @Test
  public void testEmbedMeta() throws IOException {
    GraphParser parser = GraphParser.load();
    Document document;

    document =
        parser
            .parse(
                BatchParseOption.builder()
                    .parseSchema()
                    .addResourceContent("jraphql/meta.graphqls")
                    .build())
            .getOrThrow();

    String json = JSON.stringify(document);

    System.out.println(json.length());
    ByteArrayOutputStream bs = new ByteArrayOutputStream();
    GZIPOutputStream gz = new BestGZIPOutputStream(bs);
    gz.write(json.getBytes());
    gz.finish();
    byte[] bytes = bs.toByteArray();
    String base = Base64.getMimeEncoder().encodeToString(bytes);
    System.out.println(base.length());
    String str = Pattern.compile("^", Pattern.MULTILINE).matcher(base).replaceAll("\"");
    str = Pattern.compile("$", Pattern.MULTILINE).matcher(str).replaceAll("\\\\n\"+");
    System.out.println(str);

    assertEquals(document, MetaSchema.getDocument());
  }

  public static class BestGZIPOutputStream extends GZIPOutputStream {

    public BestGZIPOutputStream(OutputStream out) throws IOException {
      super(out);
      def.setLevel(Deflater.BEST_COMPRESSION);
    }
  }
}
