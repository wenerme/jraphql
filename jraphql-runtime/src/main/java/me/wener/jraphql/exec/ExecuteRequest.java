package me.wener.jraphql.exec;

import java.util.Map;
import lombok.Data;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/17
 */
@Data
public class ExecuteRequest {
  private String query;
  private String operationName;
  private Map<String, Object> variables;
}
