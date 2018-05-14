//package me.wener.jraphql.adapter;
//
//import graphql.schema.DataFetchingEnvironment;
//import graphql.schema.idl.WiringFactory;
//import jodd.bean.BeanCopy;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import me.wener.jraphql.api.ResolverFunction;
//import me.wener.jraphql.api.ValueResolver;
//import me.wener.jraphql.runtime.ValueResolverImpl;
//
///**
// * @author <a href=http://github.com/wenerme>wener</a>
// * @since 23/03/2018
// */
//@Slf4j
//public class AbstractWiringFactory implements WiringFactory {
//
//  @SneakyThrows
//  public <IN, OUT> ValueResolver<IN, OUT> buildValueResolver(
//      ValueResolverImpl<IN, OUT> resolver, DataFetchingEnvironment environment) {
//    IN argument = null;
//
//    if (resolver.getArgumentType().getRawType() != Void.class) {
//      argument = (IN) resolver.getArgumentType().getRawType().newInstance();
//      BeanCopy.fromMap(environment.getArguments()).toBean(argument).copy();
//    }
//
//    resolver.setArgument(argument);
//    resolver.setTypeName(environment.getFieldDefinition().getType().getName());
//    resolver.setFieldName(environment.getFieldDefinition().getName());
//
//    return resolver;
//  }
//
//  protected Object fetchFallback(DataFetchingEnvironment environment) {
//    log.warn(
//        "Can not fetch {}.{}",
//        environment.getParentType().getName(),
//        environment.getFieldDefinition().getName());
//    throw new AssertionError();
//  }
//
//  /**
//   * Execute resolver function, can subclass to add pre/post process.
//   */
//  protected <IN, OUT> ValueResolver<IN, OUT> resolve(
//      ResolverFunction<IN, OUT> function, ValueResolver<IN, OUT> resolver) {
//    function.accept(resolver);
//    return resolver;
//  }
//}
