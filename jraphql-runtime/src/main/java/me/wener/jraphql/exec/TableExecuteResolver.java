package me.wener.jraphql.exec;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.Builder;
import lombok.NonNull;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/8
 */
@Builder
public class TableExecuteResolver implements FieldResolver {

  @NonNull private Table<String, String, FieldResolver> resolverTable;
  private FieldResolver fallback;

  @Override
  public Object resolve(FieldResolveContext ctx) {
    String objectName = ctx.getObjectName();
    String fieldName = ctx.getFieldName();
    FieldResolver resolver = null;
    // for meta, try field first
    if (fieldName.startsWith("__")) {
      resolver = resolverTable.get("*", fieldName);
    }
    if (resolver== null) {
      resolver = resolverTable.get(objectName, fieldName);
    }
    if (resolver == null) {
      resolver = resolverTable.get(objectName, "*");
    }
    if (resolver == null) {
      resolver = resolverTable.get("*", fieldName);
    }
    if (resolver == null) {
      resolver = resolverTable.get("*", "*");
    }
    if (resolver == null) {
      return resolveFallback(ctx);
    }
    return resolver.resolve(ctx);
  }

  protected Object resolveFallback(FieldResolveContext ctx) {
    if (fallback != null) {
      return fallback.resolve(ctx);
    }
    return null;
  }

  public static class TableExecuteResolverBuilder {
    private Table<String, String, FieldResolver> resolverTable = HashBasedTable.create();

    public TableExecuteResolverBuilder forType(FieldResolver resolver, String type) {
      resolverTable.put(type, "*", resolver);
      return this;
    }

    public TableExecuteResolverBuilder forType(FieldResolver resolver, String... types) {
      for (String type : types) {
        forType(resolver, type);
      }
      return this;
    }

    public TableExecuteResolverBuilder forField(FieldResolver resolver, String field) {
      resolverTable.put("*", field, resolver);
      return this;
    }

    public TableExecuteResolverBuilder forAll(FieldResolver resolver) {
      resolverTable.put("*", "*", resolver);
      return this;
    }

    public TableExecuteResolverBuilder forMeta(FieldResolver resolver) {
      forField(resolver, "__schema");
      forField(resolver, "__type");
      forField(resolver, "__typename");
      return this;
    }

    public TableExecuteResolverBuilder forTypeField(
        FieldResolver resolver, String type, String field) {
      resolverTable.put(type, field, resolver);
      return this;
    }
  }
}
