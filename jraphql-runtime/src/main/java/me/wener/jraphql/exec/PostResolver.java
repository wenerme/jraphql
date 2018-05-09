package me.wener.jraphql.exec;

/**
 * Post-process for one resolve
 *
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 23/03/2018
 */
public interface PostResolver {

  /**
   * @param context field context
   * @param resolver last resolver
   * @param source last resolved result
   * @return new source
   */
  Object postResolve(FieldResolveContext context, FieldResolver resolver, Object source);
}
