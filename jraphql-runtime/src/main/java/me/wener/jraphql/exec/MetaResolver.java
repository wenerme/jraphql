package me.wener.jraphql.exec;

import java.util.Objects;
import java.util.stream.Collectors;
import me.wener.jraphql.exec.Introspection.Schema;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/8
 */
public class MetaResolver implements FieldResolver {

  @Override
  public Object resolve(FieldResolveContext ctx) {

    if (ctx.getFieldName().equals("__schema")) {
      return getSchema(ctx);
    }
    if (ctx.getFieldName().equals("__type")) {
      return getSchema(ctx).getType(String.valueOf(ctx.getArguments().get("name")));
    }
    if (ctx.getFieldName().equals("__typename")) {
      return ctx.getObjectName();
    }
    if (ctx.getSource() == null) {
      return null;
    }

    switch (ctx.getObjectName()) {
      case "__Schema":
        return resolveSchema(ctx);
      case "__Type":
        return resolveType(ctx);
      case "__InputValue":
        return resolveInputValue(ctx);
      case "__Field":
        return resolveField(ctx);
      case "__Directive":
        return resolveDirective(ctx);
      case "__EnumValue":
        return resolveEnumValue(ctx);
      case "__DirectiveLocation":
      case "__TypeKind":
      case "ID":
        return String.valueOf(ctx.getSource());
      case "Int":
        return ctx.<Number>getSource().intValue();
      case "Float":
        return ctx.<Number>getSource().floatValue();
      case "String":
        return ctx.<String>getSource();
      case "Boolean":
        return ctx.<Boolean>getSource();
      default:
        throw new AssertionError();
    }
  }

  protected Schema getSchema(FieldResolveContext ctx) {
    return ctx.getExecuteContext().getTypeSystemDocument().generateIntrospection();
  }

  protected Object resolveDirective(FieldResolveContext ctx) {
    Introspection.Directive v = ctx.getSource();
    switch (ctx.getFieldName()) {
      case "name": // () -> String!
        return v.getName();
      case "description": // () -> String
        return v.getDescription();
      case "locations": // () -> [__DirectiveLocation!]!
        return v.getLocations();
      case "args": // () -> [__InputValue!]!
        return v.getArgs();
      default:
        throw new AssertionError();
    }
  }

  protected Object resolveEnumValue(FieldResolveContext ctx) {
    Introspection.EnumValue v = ctx.getSource();
    switch (ctx.getFieldName()) {
      case "name": // () -> String!
        return v.getName();
      case "description": // () -> String
        return v.getDescription();
      case "isDeprecated": // () -> Boolean!
        return v.getDeprecated();
      case "deprecationReason": // () -> String
        return v.getDeprecationReason();
      default:
        throw new AssertionError();
    }
  }

  protected Object resolveField(FieldResolveContext ctx) {
    Introspection.Field v = ctx.getSource();
    switch (ctx.getFieldName()) {
      case "name": // () -> String!
        return v.getName();
      case "description": // () -> String
        return v.getDescription();
      case "args": // () -> [__InputValue!]!
        return v.getArgs();
      case "type": // () -> __Type!
        return v.getType();
      case "isDeprecated": // () -> Boolean!
        return v.getDeprecated();
      case "deprecationReason": // () -> String
        return v.getDeprecationReason();
      default:
        throw new AssertionError();
    }
  }

  protected Object resolveInputValue(FieldResolveContext ctx) {
    Introspection.InputValue v = ctx.getSource();
    switch (ctx.getFieldName()) {
      case "name": // () -> String!
        return v.getName();
      case "description": // () -> String
        return v.getDescription();
      case "type": // () -> __Type!
        return v.getType();
      case "defaultValue": // () -> String
        return v.getDefaultValue();
      default:
        throw new AssertionError();
    }
  }

  protected Object resolveType(FieldResolveContext ctx) {
    Introspection.Type v = ctx.getSource();
    switch (ctx.getFieldName()) {
      case "kind": // () -> __TypeKind!
        return v.getKind();
      case "name": // () -> String
        return v.getName();
      case "description": // () -> String
        return v.getDescription();
      case "fields": // (includeDeprecated:Boolean=false,) -> [__Field!]
        Boolean includeDeprecated = (Boolean) ctx.getArguments().get("includeDeprecated");
        if (includeDeprecated) {
          return v.getFields();
        }
        return v.getFields().stream().filter(i -> !i.getDeprecated()).collect(Collectors.toList());
      case "interfaces": // () -> [__Type!]
        return v.getInterfaces();
      case "possibleTypes": // () -> [__Type!]
        return v.getPossibleTypes();
      case "enumValues": // (includeDeprecated:Boolean=false,) -> [__EnumValue!]
        return v.getEnumValues();
      case "inputFields": // () -> [__InputValue!]
        return v.getInputFields();
      case "ofType": // () -> __Type
        return v.getOfType();
    }
    throw new AssertionError();
  }

  protected Object resolveSchema(FieldResolveContext ctx) {
    TypeSystemDocument document = ctx.getExecuteContext().getTypeSystemDocument();
    Introspection.Schema v = ctx.getSource();
    switch (ctx.getFieldName()) {
      case "types": // () -> [__Type!]!
        return v.getTypes();
      case "type": // () -> [__Type!]!
        return v.getTypes()
            .stream()
            .filter(i -> Objects.equals(i.getName(), ctx.getArguments().get("name")))
            .findAny()
            .orElse(null);
      case "queryType": // () -> __Type!
        return v.getQueryType();
      case "mutationType": // () -> __Type
        return v.getMutationType();
      case "subscriptionType": // () -> __Type
        return v.getSubscriptionType();
      case "directives": // () -> [__Directive!]!
        return v.getDirectives();
      default:
        throw new AssertionError();
    }
  }
}