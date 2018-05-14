package me.wener.jraphql.exec.resolver;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import me.wener.jraphql.exec.FieldResolveContext;
import me.wener.jraphql.exec.FieldResolver;
import me.wener.jraphql.lang.TypeDefinition;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/8
 */
@Builder
public class TableChainFieldResolver implements FieldResolver {

  @NonNull private Table<String, String, List<FieldResolver>> resolverTable;
  private FieldResolver fallback;

  public static TableChainFieldResolverBuilder builder() {
    return new TableChainFieldResolverBuilderPrebuild();
  }

  protected Object doResolve(FieldResolveContext ctx, List<FieldResolver> resolvers) {
    if (resolvers == null) {
      return ctx.unresolved();
    }
    Object result = ctx.unresolved();
    for (FieldResolver resolver : resolvers) {
      result = resolver.resolve(ctx);
      if (!ctx.isUnresolved(result)) {
        break;
      }
    }
    return result;
  }

  @Override
  public Object resolve(FieldResolveContext ctx) {
    String objectName = ctx.getObjectName();
    String fieldName = ctx.getFieldName();
    Object result;
    // for meta, try field first
    if (fieldName.startsWith("__")) {
      if (!ctx.isUnresolved(result = doResolve(ctx, resolverTable.get("*", fieldName)))) {
        return result;
      }
    }
    if (!ctx.isUnresolved(result = doResolve(ctx, resolverTable.get(objectName, fieldName)))) {
      return result;
    }
    if (!ctx.isUnresolved(result = doResolve(ctx, resolverTable.get(objectName, "*")))) {
      return result;
    }
    if (!ctx.isUnresolved(result = doResolve(ctx, resolverTable.get("*", fieldName)))) {
      return result;
    }
    if (!ctx.isUnresolved(result = doResolve(ctx, resolverTable.get("*", "*")))) {
      return result;
    }
    if (ctx.isUnresolved(result) && fallback != null) {
      result = fallback.resolve(ctx);
    }
    return result;
  }

  static class TableChainFieldResolverBuilderPrebuild extends TableChainFieldResolverBuilder {

    @Override
    public TableChainFieldResolver build() {
      // Immutable table
      Table<String, String, List<FieldResolver>> origin = super.resolverTable;
      super.resolverTable = ImmutableTable.copyOf(super.resolverTable);
      TableChainFieldResolver build = super.build();
      super.resolverTable = origin;
      return build;
    }
  }

  public static class TableChainFieldResolverBuilder {
    private Table<String, String, List<FieldResolver>> resolverTable = HashBasedTable.create();

    public TableChainFieldResolverBuilder forType(FieldResolver resolver, String typeName) {
      getResolvers(typeName, "*").add(resolver);
      return this;
    }

    protected List<FieldResolver> getResolvers(String a, String b) {
      List<FieldResolver> resolvers = resolverTable.get(a, b);
      if (resolvers == null) {
        resolvers = Lists.newArrayList();
        resolverTable.put(a, b, resolvers);
      }
      return resolvers;
    }

    public TableChainFieldResolverBuilder forTypes(FieldResolver resolver, String... typeNames) {
      for (String type : typeNames) {
        forType(resolver, type);
      }
      return this;
    }

    public TableChainFieldResolverBuilder forTypes(
        FieldResolver resolver, List<TypeDefinition> types) {
      for (TypeDefinition definition : types) {
        if (definition.getName() == null) {
          continue;
        }
        forType(resolver, definition.getName());
      }
      return this;
    }

    public TableChainFieldResolverBuilder forField(FieldResolver resolver, String fieldName) {
      getResolvers("*", fieldName).add(resolver);
      return this;
    }

    public TableChainFieldResolverBuilder forAll(FieldResolver resolver) {
      getResolvers("*", "*").add(resolver);
      return this;
    }

    public TableChainFieldResolverBuilder forMeta(FieldResolver resolver) {
      forField(resolver, "__schema");
      forField(resolver, "__type");
      forField(resolver, "__typename");
      return this;
    }

    public TableChainFieldResolverBuilder forTypeField(
        FieldResolver resolver, String type, String field) {
      getResolvers(type, field).add(resolver);
      return this;
    }
  }
}
