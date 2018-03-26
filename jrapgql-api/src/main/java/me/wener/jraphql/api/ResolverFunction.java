package me.wener.jraphql.api;

import java.util.function.Consumer;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 26/03/2018
 */
@FunctionalInterface
public interface ResolverFunction<IN, OUT> extends Consumer<ValueResolver<IN, OUT>> {

}
