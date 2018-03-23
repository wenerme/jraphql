package me.wener.jraphql.lang;

import java.util.List;
import javax.validation.constraints.NotNull;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
public interface TypeExtension extends Node {

  @NotNull
  String getExtendTypeName();

  List<Directive> getDirectives();
}
