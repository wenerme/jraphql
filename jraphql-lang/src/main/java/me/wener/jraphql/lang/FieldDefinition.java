package me.wener.jraphql.lang;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.Data;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
@Data
public class FieldDefinition extends AbstractDefinition<FieldDefinition>
    implements HasDescription<FieldDefinition>,
        HasName<FieldDefinition>,
        HasDirectives<FieldDefinition>,
        HasType<FieldDefinition>,
        HasArgumentDefinitions<FieldDefinition> {

  private String description;
  private String name;
  private List<InputValueDefinition> argumentDefinitions = Lists.newArrayList();
  private Type type;
  private List<Directive> directives = Lists.newArrayList();
}
