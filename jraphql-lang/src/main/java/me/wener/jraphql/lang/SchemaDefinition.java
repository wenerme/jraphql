package me.wener.jraphql.lang;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.Data;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
@Data
public class SchemaDefinition extends AbstractDefinition
    implements HasDirectives<SchemaDefinition> {

  private List<Directive> directives = Lists.newArrayList();
  private String queryTypeName;
  private String mutationTypeName;
  private String subscriptionTypeName;
}
