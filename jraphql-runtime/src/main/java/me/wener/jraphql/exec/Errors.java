package me.wener.jraphql.exec;

import me.wener.jraphql.lang.GraphLanguageException;
import me.wener.jraphql.lang.SourceLocation;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/13
 */
class Errors {
  public static RuntimeException typeMismatch(
      String expected, String actually, SourceLocation location) {
    return new GraphLanguageException(
        String.format(
            "`%s` type expected but found `%s` @ %s", expected, actually, location.toLocation()));
  }

  public static RuntimeException typeNotFound(String name) {
    return new GraphLanguageException(String.format("type '%s' not found", name));
  }

  public static RuntimeException typeResolveFailed(String name) {
    return new GraphExecuteException("can not resolve type " + name);
  }
}
