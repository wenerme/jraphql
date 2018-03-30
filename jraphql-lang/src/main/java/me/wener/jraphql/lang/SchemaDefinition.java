package me.wener.jraphql.lang;

import lombok.Data;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
@Data
public class SchemaDefinition extends AbstractTypeDefinition<SchemaDefinition> {

  private String queryTypeName;
  private String mutationTypeName;
  private String subscriptionTypeName;
}
