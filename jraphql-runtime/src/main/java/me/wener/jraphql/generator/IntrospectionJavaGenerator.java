package me.wener.jraphql.generator;

import com.google.common.collect.Maps;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import me.wener.jraphql.exec.Introspection.Type;
import me.wener.jraphql.exec.Introspection.TypeKind;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/5/22
 */
public class IntrospectionJavaGenerator {

  @Setter @Getter private String nonnull = "@NotNull";

  @Getter
  private final Map<String, String> typeMap = Maps.newHashMap();

  public CharSequence generateJavaType(Type type) {
    if (type.getName() != null) {
      return typeMap.getOrDefault(type.getName(), type.getName());
    }
    if (type.getKind() == TypeKind.NON_NULL) {
      return nonnull + " " + generateJavaType(type.getOfType());
    }
    return "List<" + generateJavaType(type.getOfType()) + ">";
  }
}
