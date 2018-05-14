package me.wener.jraphql.exec.resolver;

import com.github.wenerme.wava.util.Later;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import lombok.RequiredArgsConstructor;
import me.wener.jraphql.exec.FieldResolveContext;
import me.wener.jraphql.exec.FieldResolver;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/5/14
 */
@RequiredArgsConstructor
public class ChainFieldResolver implements FieldResolver {
  private final List<FieldResolver> resolvers;

  @Override
  public Object resolve(FieldResolveContext ctx) {
    return new Chainner(resolvers.iterator(), ctx).next(ctx.unresolved());
  }

  @RequiredArgsConstructor
  private static class Chainner {
    private final Iterator<FieldResolver> iterator;
    private final FieldResolveContext context;

    public CompletionStage<Object> next(Object source) {
      if (!iterator.hasNext() || !context.isUnresolved(source)) {
        return CompletableFuture.completedFuture(source);
      }
      return Later.asCompletionStage(iterator.next().resolve(context)).thenCompose(this::next);
    }
  }
}
