package me.wener.jraphql.api;

import com.google.common.reflect.TypeToken;
import java.util.concurrent.CompletionStage;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 23/03/2018
 */
public interface ValueResolver<IN, OUT> {

  IN getArgument();

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
