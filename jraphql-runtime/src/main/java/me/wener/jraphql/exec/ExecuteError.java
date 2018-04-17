package me.wener.jraphql.exec;

import java.util.List;
import lombok.Data;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/17
 */
@Data
public class ExecuteError {
  private String message;
  private List<String> path;
//  @JsonIgnore private transient Throwable exception;
}
