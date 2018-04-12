package me.wener.jraphql.exec;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.Data;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 27/03/2018
 */
@Data
public class ExecuteResult {

  private Object data;

  @JsonInclude(content = Include.NON_EMPTY)
  private List<Throwable> errors;
}
