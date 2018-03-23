package me.wener.jraphql.lang;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.Data;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
@Data
public class EnumTypeExtension extends AbstractTypeExtension
    implements HasEnumValueDefinitions<EnumTypeExtension> {

  private List<EnumValueDefinition> enumValueDefinitions = Lists.newArrayList();
}
