package me.wener.jraphql.lang;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/2
 */
public enum TypeDefinitionKind implements Kind {
  SCALAR,
  ENUM,
  OBJECT,
  INTERFACE,
  SCHEMA,
  UNION,
  INPUT_OBJECT,
  DIRECTIVE,
  ;
}
