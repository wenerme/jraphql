package me.wener.jraphql.exec;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import me.wener.jraphql.lang.TypeDefinition;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/8
 */
@Builder
public class TableFieldResolverRegistry implements FieldResolverRegistry {

  @NonNull private Table<String, String, FieldResolver> resolverTable;
  private FieldResolver fallback;

  @Override
  public FieldResolver lookup(FieldResolveContext ctx) {
    String objectName = ctx.getObjectName();
    String fieldName = ctx.getFieldName();
    FieldResolver resolver = null;
    // for meta, try field first
    if (fieldName.startsWith("__")) {
      resolver = resolverTable.get("*", fieldName);
    }
    if (resolver == null) {
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
      resolver = fallback;
    }
    if (resolver == null) {
      resolver = FieldResolver.unresolved();
    }
    return resolver;
  }

  protected Object resolveFallback(FieldResolveContext ctx) {
    if (fallback != null) {
      return fallback.resolve(ctx);
    }
    return null;
  }

  public static class TableFieldResolverRegistryBuilder {
    private Table<String, String, FieldResolver> resolverTable = HashBasedTable.create();

    public TableFieldResolverRegistryBuilder forType(FieldResolver resolver, String typeName) {
      resolverTable.put(typeName, "*", resolver);
      return this;
    }

    public TableFieldResolverRegistryBuilder forTypes(FieldResolver resolver, String... typeNames) {
      for (String type : typeNames) {
        forType(resolver, type);
      }
      return this;
    }

    public TableFieldResolverRegistryBuilder forTypes(
        FieldResolver resolver, List<TypeDefinition> types) {
      for (TypeDefinition definition : types) {
        if (definition.getName() == null) {
          continue;
        }
        forType(resolver, definition.getName());
      }
      return this;
    }

    public TableFieldResolverRegistryBuilder forField(FieldResolver resolver, String fieldName) {
      resolverTable.put("*", fieldName, resolver);
      return this;
    }

    public TableFieldResolverRegistryBuilder forAll(FieldResolver resolver) {
      resolverTable.put("*", "*", resolver);
      return this;
    }

    public TableFieldResolverRegistryBuilder forMeta(FieldResolver resolver) {
      forField(resolver, "__schema");
      forField(resolver, "__type");
      forField(resolver, "__typename");
      return this;
    }

    public TableFieldResolverRegistryBuilder forTypeField(
        FieldResolver resolver, String type, String field) {
      resolverTable.put(type, field, resolver);
      return this;
    }
  }
}
