package me.wener.jraphql.exec.resolver;

import com.github.wenerme.wava.util.Later;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import lombok.RequiredArgsConstructor;
import me.wener.jraphql.exec.FieldResolveContext;
import me.wener.jraphql.exec.PostResolver;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/5/14
 */
@RequiredArgsConstructor
public class ChainPostResolver implements PostResolver {
  private final List<PostResolver> resolvers;

  @Override
  public Object postResolve(FieldResolveContext context, Object source) {
    return new Chainner(resolvers.iterator(), context).next(source);
  }

  @RequiredArgsConstructor
  private static class Chainner {
    private final Iterator<PostResolver> iterator;
    private final FieldResolveContext context;

    public CompletionStage<Object> next(Object source) {
      if (!iterator.hasNext()) {
        return CompletableFuture.completedFuture(source);
      }
      return Later.asCompletionStage(iterator.next().postResolve(context, source))
          .thenCompose(this::next);
    }
  }
}
