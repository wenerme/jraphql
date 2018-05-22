package me.wener.jraphql.exec;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/11
 */
public abstract class ExecutableContext {

  private final Map<Object, Object> context = Maps.newConcurrentMap();

  public <T> T remove(Object key) {
    return (T) context.remove(key);
  }

  public <T> T put(Object key, T value) {
    return (T) context.put(key, value);
  }

  public <T> T get(Object key) {
    Object o = context.get(key);
    if (o == null) {
      return getParent().get(key);
    }
    return (T) o;
  }

  public abstract Execution getExecution();

  public TypeSystemDocument getTypeSystemDocument() {
    return getExecution().getTypeSystemDocument();
  }

  public ExecutableDocument getExecutableDocument() {
    return getExecution().getExecutableDocument();
  }

  public void collectPath(Consumer<String> consumer) {
    if (getParent() != null) {
      getParent().collectPath(consumer);
    }
  }

  public List<String> collectPath() {
    Builder<String> builder = ImmutableList.builder();
    collectPath(builder::add);
    return builder.build();
  }

  public abstract ExecutableContext getParent();

  public ExecutorService getExecutorService() {
    return getExecution().getExecutorService();
  }
}
