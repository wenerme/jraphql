package me.wener.jraphql.lang;

import java.util.List;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
public interface TypeExtension extends DocumentDefinition {

  /** @return name of this extension */
  String getName();

  /** @return type has been extended */
  String getExtendTypeName();

//  /** @return extend by another type */
//  default String getExtendByName() {
//    // extend <ExtendTypeName> by <ExtendByName> as <Name>
//    // TODO
//    return null;
//  }

//  default String getDescription(){
//    // TODO if extension can be named, thet also can be described.
//    return null;
//  }

  List<Directive> getDirectives();

  default TypeDefinitionKind getKind() {
    return Langs.getTypeDefinitionKind(this);
  }
}
