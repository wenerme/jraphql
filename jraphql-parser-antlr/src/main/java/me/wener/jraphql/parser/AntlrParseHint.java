package me.wener.jraphql.parser;

import com.google.common.collect.Maps;
import java.util.Map;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/11
 */
public interface AntlrParseHint {

  static AntlrParseHint create() {
    return of(Maps.newHashMap());
  }

  static AntlrParseHint of(Map<String, Object> hints) {
    return () -> hints;
  }

  Map<String, Object> getHints();

  default boolean getTrimParseTree() {
    return Boolean.TRUE.equals(getHints().get("antlr.TrimParseTree"));
  }

  /** @see org.antlr.v4.runtime.Parser#setTrimParseTree(boolean) */
  default AntlrParseHint setTrimParseTree(Boolean v) {
    getHints().put("antlr.TrimParseTree", v);
    return this;
  }
}
