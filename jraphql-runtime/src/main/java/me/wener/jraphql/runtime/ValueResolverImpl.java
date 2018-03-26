package me.wener.jraphql.runtime;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.wener.jraphql.api.ValueResolver;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 21/03/2018
 */
@Getter
@Setter
public class ValueResolverImpl<IN, OUT> implements ValueResolver<IN, OUT> {

  private final TypeToken<IN> argumentType = new TypeToken<IN>(getClass()) {
  };
  private final TypeToken<OUT> valueType = new TypeToken<OUT>(getClass()) {
  };
  private final CompletableFuture<OUT> feature = new CompletableFuture<>();
  private IN argument;
  private String fieldName;
  private String typeName;

  @Setter(AccessLevel.PRIVATE)
  private boolean resolved;

  @Override
  public IN getArgument() {
    return argument;
  }

  @Override
  public void resolve(OUT value) {
    checkResolved();
    feature.complete(value);
    resolved = true;
  }

  @Override
  public void resolve(CompletionStage<OUT> value) {
    checkResolved();
    value.whenComplete(
      (v, e) -> {
        if (e != null) {
          feature.completeExceptionally(e);
        } else {
          feature.complete(v);
        }
      });
    resolved = true;
  }

  @Override
  public OUT getValue() {
    return feature.getNow(null);
  }

  @Override
  public CompletionStage<OUT> getFeature() {
    return feature;
  }

  @Override
  public void exception(Throwable throwable) {
    checkResolved();
    feature.completeExceptionally(throwable);
    resolved = true;
  }

  protected void checkResolved() {
    Preconditions.checkState(!resolved, "Value already resolved");
  }
}
