package me.wener.jraphql.exec;

import java.util.Map;
import lombok.Data;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 27/03/2018
 */
@Data
public class Response {

  private Object data;
  private GraphExecuteException errors;
  private Map<String, Object> extensions;
}
