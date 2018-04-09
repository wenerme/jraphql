package me.wener.jraphql.lang;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
public interface TypeDefinition extends DocumentDefinition {

  /** @return name to the type */
  @Nonnull
  String getName();

  String getDescription();

  List<Directive> getDirectives();

  default TypeDefinitionKind getKind() {
    return Langs.getTypeDefinitionKind(this);
  }
}
