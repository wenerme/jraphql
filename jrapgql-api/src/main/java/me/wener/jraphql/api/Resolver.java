package me.wener.jraphql.api;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 23/03/2018
 */
public interface Resolver {

  default <IN, OUT> void resolve(ValueResolver<IN, OUT> resolver) {
    resolver.exception(new UnsupportedOperationException("Unimplemented"));
  }
}
