package me.wener.jraphql.lang;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.Data;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
@Data
public class EnumValueDefinition extends AbstractDefinition
    implements HasDirectives<EnumValueDefinition>,
        HasDescription<EnumValueDefinition>,
        HasEnumValue<EnumValueDefinition> {

  private String description;
  private String enumValue;
  private List<Directive> directives = Lists.newArrayList();
}
