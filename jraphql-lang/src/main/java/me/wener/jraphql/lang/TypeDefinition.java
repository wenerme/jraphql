package me.wener.jraphql.lang;

import java.util.List;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
public interface TypeDefinition extends DocumentDefinition {
  String getName();

  String getDescription();

  List<Directive> getDirectives();

  default TypeDefinitionKind getKind() {
    return Langs.getTypeDefinitionKind(this);
  }
}
