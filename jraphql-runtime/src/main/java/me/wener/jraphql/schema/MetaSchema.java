package me.wener.jraphql.schema;

import com.github.wenerme.wava.util.JSON;
import com.google.common.base.Suppliers;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.function.Supplier;
import java.util.zip.GZIPInputStream;
import lombok.SneakyThrows;
import me.wener.jraphql.lang.Document;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/3
 */
public class MetaSchema {
  private static final String BASE64 =
      "H4sIAAAAAAAAAM1dbXPbNhL+Kxh9uXRGlkXqPfdJjeVUrSM3funNTZuzIAqy2FAgwxc7mo7/excA\n"
          + "SZGyZRMgoN50prEkCrt4drG7WCxWfzXu4m1AGu8bZ76TbAiNG81G5CehQy58B8euTxvv/2p4LoVn\n"
          + "2s2G43vJhvI/xVPwzTChsbshjadmY0lWLnXZt6LG+9//yge/drCHwxt4cZY/8Qoh+2VCGxLj1n2I\n"
          + "g/U3L2LkHH/DWC7T+iDefGV4q/LwNGYjvW+gmzVB8ymN5yjiU0GMGApJEJKIcYCoT09WIXYYJeyh\n"
          + "yL2nZIke175HEAVgQ9dBD9hLSNRCMA5yMN19Pf0ELUj8SAhFJ+/s/3WsHxCmS8T+QifIajWevjQb\n"
          + "FG8YqzACMKiMb88svl1FfM89Hx9GOIV06ScLj5zA244bAVlUQD2FEcPDAXy8cuH5xRb9Pp1MJmjQ\n"
          + "6355t47j4P3pKaGtR/erG5Cli1t+eH/KXp2yx+5WjAmX3t8FvkvjH0qwcwbrAD80C/xAEfjrOIQZ\n"
          + "H0Q+Jt/jBOBd4hg3dx8AuID07c35yRA5a8zEQEIUkW8JoQ7Tcza2GFoM6UZo40cx8ldAHSWRkM5H\n"
          + "xtjnCxT7hQWxCgk5WfnhBq2TDaYnIcFLDHLnvJRkIgjUEYplGTY3bUWx/OiD9cD0oFzmcZiQOfJD\n"
          + "NF9hLyLzEjDpt2sh0zWMTEfVEp8dBAWjhLqggshdwmtmA8JmUeO4mq1I7KzBvCJ/8SdxYgYhqPJX\n"
          + "skUr9idYZ2dNhAJPzwQJHAQEhxFyKXz+8/XlDIaJAnB0hH0Vp4r+b7T2H8kDo+nGTOGpD/8w5peC\n"
          + "9oLsaXQL/WcNvJHvYLHSJQV8uTRIYk64CS+3KBLL6F2UML5B9H80un805j8wztnw97Dydh924QMx\n"
          + "AreI6NH1PEYZOw4JdkSmZ2WnclbSlTMXTGzsPlRUlYFhVenLq4qYAdgvECP5TpwkBrRABi51vGQJ\n"
          + "tmQNAgIF8ZYMRvAj94wJ5FNvC54bhMK+OHdXc4TDex4cMYmyRVfCLR0OmPdSttnkGtPZxXQ2uTu/\n"
          + "Gn/8NJndwMfZn3fXv15Nxmfsnenk4qwBQ2UEzg4EUFMmzd+YMKtJY7RDq2tCGsPK4+fSmAqYlim0\n"
          + "z1BcAT3BwI6TmU9niecxg1VxtsPXuHk2PJBe6hp83+w+PX1RX052x+xysm19yymCQOrQWpJYRmyY\n"
          + "/6c1ZPfMriG7K7+GrgGjwMQSKk5W+xKqPLjeJTQyvISG8kvoEw6/ctdLPCJ8zQpChywCjiDo2GDm\n"
          + "nKmPPJ8ypx4lQeCH4LNLkl6yeAcYIsv9FTOZ3X66+218cTvJlsbd2eR8OpveTC9nBlZJp2N2lXTa\n"
          + "8qtk8j3wsEsjWCZbYZgyuB8B2x12TYgIYV8D3h6i5ywsYGEWBtjv70nEWSlqYAV+LXl+WcwJYSOz\n"
          + "pSxCi6Kd1GHXu3FZmMv2Xi10DjsiHLP3XWnGbHnGfmf6uvQfqdg4R7BzXmIWiK5gJS4AuRYl8WkQ\n"
          + "+iyIjk436dOn5V0zRLoR1yEV21HUL8uuYDzy/SBLRuHEE7pcJCueEG9XI2xbrxF+EATA0j5btI09\n"
          + "C3bJtxuV919dw4mwjkJQPUa5GUYg+QfYZrHtzyPeMv2FF07osq2GF5OQwipDaXowDRlYxoaltfie\n"
          + "CpBzl5wn2J2s8YPLdzMFi7hMk5ItWWWXz4TIUlCw/lOKIh+QcHBEoiba+gmiRGwLUySRH3CLzE0B\n"
          + "QzBD4l9RAb8MK1mWR/IsgzRAsllkl/mOSOwo2d42SlYr14Fdarb5hC8vXZGPA8O6s6nS7HYV0iYs\n"
          + "hgyEBecct3bKGuUYc4+w2Ga6yh9fZjwDxytuZOVNf1chrwxyLobTJbt5d5czD1T5fA457fPyh68x\n"
          + "WdVfp0zwf1SjyiIxq607rKw++p5reCrKVQK6rhx0Qr+CdFSlGRYosgRL9RmqTbAnN8Fd4KmsIAWK\n"
          + "Vk9GQS7cKDY2uALzfe3aXXn05+YiH+3pSVnZ+3K6AK6hhhr0Ve1EFTVQHlyBeUu7GlQePVeD3Q5O\n"
          + "yL8UgU5osql+Tmj4ZKQ7qhd/skNUFmsu/8QO296BN92wbHmAQ4hRYG/NfGsWS3qY3if4niXUJR17\n"
          + "TyESeWE15sExsEaJCJgCP4pcdq6VzsFxSXQoCMgZazYISHFvj17GfvL8gdcmaHgT31PYe+aoFaWL\n"
          + "0beEhFsIk0nIPy1h9fl2cvXfxr62S8BgOOPX62qDYZPE4u2Xkfh0ezPmCZ8aYAwMg9HXBkaULPJI\n"
          + "6wAg17c/Xn+4mv5aFxTD5yq9oTZQxP6nVL3AE+Xqk+9bZiffb+ubfHb+sCtIKkORnSIUcqM1gDFs\n"
          + "Pvu2fmCigB1AvwxKerRSAxDDhrSvzZCyk3Y2Zg5M+UR87/ipBiKGrWlfozUVpxAHVs71h58mn8Z1\n"
          + "kDBsQvtDjUjwCpODSIwvxlc1kBgYtqeDtr5VktbJ8KztATwuf/x58qHOGhkYNqMDW69zPehc9k/d\n"
          + "akBi2JAONBrS/HD/ACzjq4+3+nzuwLBBHfR1upiYhCt45xA009nN5Op8/GFSBxDDdnWgz64mVOyG\n"
          + "X8TidlZPMYaGrepQo1Vl2/hDOLCD/DowGDamQ1szDKJS8RUw0qqGGpAYNqZDrVEpq96s4HWns19v\n"
          + "b+5q+96hYXM67BvC5lVPLMB57o/rnMmPDFfmjRSSjZewgckziWIhiTrme/eBUMRUocX/n9+QCElW\n"
          + "Ji3eafLDXNnE6EjhxDPwQJZr31uSMGUyrW2Gv0sXZVroJ1FKzWS+Y57VDoYkTkKqUgUzUqh7fLHc\n"
          + "WzC9l6rN15y+89pR94jntUVi+s9rq4+u57x21Dv2eW2R4hHOa0eSZ3RudFasG1TUkeKBlPZS0eqj\n"
          + "P68VVcNwIKskAkEY5KpOXVuRLiv2rq4qdZyX1TZ9tbKt4r6EK2dlYdN8q8RICkeVHZvx22IYeS6/\n"
          + "Qoa4jMFxEeys2evHtQt/rHEk6RKstqXiE5hYmuB12TuuKCLNWcvrpJp8Ujh1WHxOey6Dz0Kfu7Da\n"
          + "x6zvKVHT7zAkhtfjMaz20Ut8SiSP4DOsdu+IhR0latorO2qMrsK+pV+/e7WLOxSVQDJyiMVEVFHs\n"
          + "m7USfUkrcXfHh1RHb/APxF0lqvoDL4nhNUVeVnv4z4ReJcJHjL2M36a2FAKZcV7SnZZH83J0EVux\n"
          + "LXmhfpqX7a9Jmn9ZpY9AtCO7CbcsW6V4HqhmkWJI9tsQ8I/RbyK9IULB/C4CMO2mF8VhDtLMKlxS\n"
          + "F2X8aVSY3kNJsxrl8K9gzvXFgFbvmDGg1TNq3asPrykGtPpHjwGLJI8RA1qDY7r/IjUTCjI4svs3\n"
          + "frle5Xb9OKtzPVnldwTTxGpuKXkjljUpGySehOVXYgodI/Yumxbu0akKaSQZXehxubbhTkeWyj36\n"
          + "XFToulBqQ8TFegcHeOF6MDMS7d0OJuEDCVtoGrNmIX7EPLLnyXozW8Gb4QfseqL7jkjKQCCw3MUF\n"
          + "vrjhL/hrMmf8SDyP/cuvNtE43CLeTCliuibNsEI3KV4k3MyLZEUW5uUq0f1qayESjc7YNnyyZtl9\n"
          + "FWuRp6pAUkKquzvHi/TWdKpxRYD4o+quoAiG3H66SjpAfXQV9m3tnqz68M88maovs037MlvBl7G2\n"
          + "Szxaj9c43q/4j/JuRqHv8+C/XLPJH7+pFbAUQZG8OFZJzJWH1xSwdAyXxFgqTRqmq6KRyaxPVDDa\n"
          + "cUkNnt94eEsTsm8cUoYqsipCVyluyWWlKKqOaVHZukRVcqj70jrgbN+QWPFbtaRW7N1gH0FqPdNS\n"
          + "69b18YV4rZKj3z2vbkc7yhdkq3h79dFV2B9odwMd2WKBYj+A5xc7ZbdG3b7p3o5DteaOq4QuMRsc\n"
          + "e6xGSWgw3e5vmtwoNzm8UWNIeFqQ3//86lKempTda6h0yBDBs0tz/nBUyk3yNUbQXJiYX4CzOS94\n"
          + "lG1pYin0w5AmoZBAPiMBoaLDh9gHMvDFzlW0r3RIGGPAJ00Y51nZQqMNhBc+a3e53uuuW4VlW01k\n"
          + "LXS96x+66w1C/RJfC7L1+QE+W4Ji30sKPu9x7XpEmmOFHTgvfytzKpLaWUvpV0ooCl9IZSDNscIW\n"
          + "HKjlHVYA7PEiilmDYMFTE93SrBFQzm6zxOnlrvRVnt+eQllifhE7zjoWtRBzQ5zJ1GOkiIJFZPkX\n"
          + "5AOjqQLtZRFS268rh9CVzNeyJajutrtm87VdtXwts5zqW6DuUM+RSKUJDlVPLBTnNjr6gUWR5DEO\n"
          + "LHptuSmmdk5/X90iI5UqNtLmwK8XAFSBvEi505Y5nz/cCS995K1WeGX42xV64fF25E/MJKpE9iVy\n"
          + "QwORfYnASLuFqz78Xhmgeh6vZ0mWpGRe96XtXSUZWYZ3XyUC+ndf1Yd/nmtVFJEtJ6IsIrk5kHKv\n"
          + "JKUCTWtkQkoFArb+WKH68NqkJFk/m3fiMeJtOpKZYn3epkC50z2ytymKoGve2xTI9WwTa6RIoKN/\n"
          + "jVQe/oV7SjUWSlfW44Dun2dxmZKgusoeoaKgukYrLKsPf6jaV1FSkuVg/qpO7r1ITe4QU71PndUz\n"
          + "3Ovd6ikkM8f5Be+8z+wjOxvJEmSiIDG9o5nmB+fIfSmX8IvY0Gtu+2aZ7uhkqbR0mtKly3xXJM4m\n"
          + "sl+Oytqx6O3BYpnu3WSpNG86BEHWh6WF5mKHO+fZqfkump/zJDjvq50m+/T2aLFMt3ayVHo7HcYr\n"
          + "R2YfslJ0/QZqOtpzWKY7QFkqLaAOrjXeoqP1DCb+WQEnrZ07LNOtoaz+UKdu8VMcNN/tAN7Ap2ZH\n"
          + "D8t0vyhr0Na79HZtGlrMRuXx3xs4aWppYZnuJ2UNNJp2flgPMIng6w2ELqbX9ZAxbcQHGo04/2lV\n"
          + "CuF7VXRml7O72e3FRb0+H9awbTimHKj8gBB89pkXxGU/ZnhPYnaGFvrsJ09Fm2Ecs0aP/iat65A9\n"
          + "PhsonILzt91vnijwecQhzU695ygIAe/v5aA2n4fGUzKJlkSKP3qn8Ns+H0E8ThKGvOemqC0uARFl\n"
          + "9caKe9bipK2u9j1r9eH3K6iVt6sSbZQUxdiRF+O5y8sAuFFabHk5QFmO6RUZ3RnAIhiVzpvq3eUq\n"
          + "kevr16au5G8a7O5yfdFA0u6bLwU03UjLkuhilmvvlWg/kdkhRowXtLgU8ae+S5fdqPTz+oRd9suq\n"
          + "q/Qna0WNCrzge43nq6mmJg/MavJAXZPhv78BatgeOHd/AAA=";

  private static final Supplier<Document> DOCUMENT_SUPPLIER =
      Suppliers.memoize(MetaSchema::parseDoc);

  public static Document getDocument() {
    return DOCUMENT_SUPPLIER.get();
  }

  @SneakyThrows
  private static Document parseDoc() {
    byte[] bytes = Base64.getMimeDecoder().decode(BASE64);
    GZIPInputStream stream = new GZIPInputStream(new ByteArrayInputStream(bytes));
    return JSON.parse(ByteStreams.toByteArray(stream), Document.class);
  }
}
