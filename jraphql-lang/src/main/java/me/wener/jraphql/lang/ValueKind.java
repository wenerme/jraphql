package me.wener.jraphql.lang;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/4
 */
public enum ValueKind implements Kind {
  NULL,
  FLOAT,
  OBJECT,
  LIST,
  INT,
  ENUM,
  STRING,
  BOOLEAN,
  VARIABLE,
  ;
}
