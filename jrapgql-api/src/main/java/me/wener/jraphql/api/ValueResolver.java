package me.wener.jraphql.api;

import com.google.common.reflect.TypeToken;
import java.util.concurrent.CompletionStage;

/**
 * Used to resolve a value
 *
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 23/03/2018
 */
public interface ValueResolver<IN, OUT> {

  /**
   * Argument for field
   */
  IN getArgument();

  /** @return Resolved value, if not done, will return {@code null} */
  OUT getValue();

  void resolve(OUT value);

  void resolve(CompletionStage<OUT> value);

  boolean isResolved();

  CompletionStage<OUT> getFeature();

  void exception(Throwable throwable);

  String getFieldName();

  String getTypeName();

  TypeToken<IN> getArgumentType();

  TypeToken<OUT> getValueType();
}
