package me.wener.jraphql.parser.antlr;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.wener.jraphql.lang.Argument;
import me.wener.jraphql.lang.BooleanValue;
import me.wener.jraphql.lang.Builders;
import me.wener.jraphql.lang.Builders.BuildArgumentDefinitions;
import me.wener.jraphql.lang.Builders.BuildArguments;
import me.wener.jraphql.lang.Builders.BuildDefaultValue;
import me.wener.jraphql.lang.Builders.BuildDescription;
import me.wener.jraphql.lang.Builders.BuildDirectives;
import me.wener.jraphql.lang.Builders.BuildEnumValue;
import me.wener.jraphql.lang.Builders.BuildEnumValueDefinitions;
import me.wener.jraphql.lang.Builders.BuildFieldDefinitions;
import me.wener.jraphql.lang.Builders.BuildInputFieldsDefinitions;
import me.wener.jraphql.lang.Builders.BuildInterfaces;
import me.wener.jraphql.lang.Builders.BuildName;
import me.wener.jraphql.lang.Builders.BuildSelectionSet;
import me.wener.jraphql.lang.Builders.BuildType;
import me.wener.jraphql.lang.Builders.BuildTypeCondition;
import me.wener.jraphql.lang.Builders.BuildTypeExtension;
import me.wener.jraphql.lang.Builders.BuildValue;
import me.wener.jraphql.lang.Comment;
import me.wener.jraphql.lang.Directive;
import me.wener.jraphql.lang.DirectiveDefinition;
import me.wener.jraphql.lang.Document;
import me.wener.jraphql.lang.DocumentDefinition;
import me.wener.jraphql.lang.EnumTypeDefinition;
import me.wener.jraphql.lang.EnumTypeExtension;
import me.wener.jraphql.lang.EnumValue;
import me.wener.jraphql.lang.EnumValueDefinition;
import me.wener.jraphql.lang.Field;
import me.wener.jraphql.lang.FieldDefinition;
import me.wener.jraphql.lang.FloatValue;
import me.wener.jraphql.lang.FragmentDefinition;
import me.wener.jraphql.lang.FragmentSpread;
import me.wener.jraphql.lang.GraphLanguageException;
import me.wener.jraphql.lang.InlineFragment;
import me.wener.jraphql.lang.InputObjectTypeDefinition;
import me.wener.jraphql.lang.InputObjectTypeExtension;
import me.wener.jraphql.lang.InputValueDefinition;
import me.wener.jraphql.lang.IntValue;
import me.wener.jraphql.lang.InterfaceTypeDefinition;
import me.wener.jraphql.lang.InterfaceTypeExtension;
import me.wener.jraphql.lang.ListType;
import me.wener.jraphql.lang.ListValue;
import me.wener.jraphql.lang.NamedType;
import me.wener.jraphql.lang.Node;
import me.wener.jraphql.lang.NonNullType;
import me.wener.jraphql.lang.NullValue;
import me.wener.jraphql.lang.ObjectTypeDefinition;
import me.wener.jraphql.lang.ObjectTypeExtension;
import me.wener.jraphql.lang.ObjectValue;
import me.wener.jraphql.lang.OperationDefinition;
import me.wener.jraphql.lang.ScalarTypeDefinition;
import me.wener.jraphql.lang.ScalarTypeExtension;
import me.wener.jraphql.lang.SchemaDefinition;
import me.wener.jraphql.lang.Selection;
import me.wener.jraphql.lang.SelectionSet;
import me.wener.jraphql.lang.SourceLocation;
import me.wener.jraphql.lang.StringValue;
import me.wener.jraphql.lang.Type;
import me.wener.jraphql.lang.UnionTypeDefinition;
import me.wener.jraphql.lang.UnionTypeDefinition.UnionTypeDefinitionBuilder;
import me.wener.jraphql.lang.UnionTypeExtension;
import me.wener.jraphql.lang.UnionTypeExtension.UnionTypeExtensionBuilder;
import me.wener.jraphql.lang.Value;
import me.wener.jraphql.lang.Variable;
import me.wener.jraphql.lang.VariableDefinition;
import me.wener.jraphql.parser.antlr.GraphQLParser.ArgumentContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.ArgumentsContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.ArgumentsDefinitionContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.BooleanValueContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.DefaultValueContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.DefinitionContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.DescriptionContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.DirectiveContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.DirectiveDefinitionContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.DirectiveLocationsContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.DirectivesContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.DocumentContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.EnumTypeDefinitionContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.EnumTypeExtensionContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.EnumValueContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.EnumValueDefinitionContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.EnumValuesDefinitionContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.ExecutableDefinitionContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.ExecutableDocumentContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.FieldContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.FieldDefinitionContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.FieldsDefinitionContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.FloatValueContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.FragmentDefinitionContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.FragmentSpreadContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.GraphqlContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.ImplementsInterfacesContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.InlineFragmentContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.InputFieldsDefinitionContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.InputObjectTypeDefinitionContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.InputObjectTypeExtensionContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.InputValueDefinitionContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.IntValueContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.InterfaceTypeDefinitionContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.InterfaceTypeExtensionContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.ListTypeContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.ListValueContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.NameContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.NamedTypeContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.NonNullTypeContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.NullValueContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.ObjectFieldContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.ObjectTypeDefinitionContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.ObjectTypeExtensionContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.ObjectValueContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.OperationDefinitionContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.OperationTypeDefinitionContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.ScalarTypeDefinitionContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.ScalarTypeExtensionContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.SchemaDefinitionContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.SelectionContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.SelectionSetContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.StringValueContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.TypeConditionContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.TypeContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.TypeSystemDefinitionContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.TypeSystemDocumentContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.UnionMemberTypesContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.UnionTypeDefinitionContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.UnionTypeExtensionContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.ValueContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.VariableContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.VariableDefinitionContext;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
@Slf4j
@Setter
@Getter
public class GraphQLLangVisitor extends me.wener.jraphql.parser.antlr.GraphQLBaseVisitor<Node> {

  private final SourceLocation.SourceLocationBuilder sourceLocationBuilder =
      SourceLocation.builder();
  private final Comment.CommentBuilder commentBuilder = Comment.builder();
  private String source = "inline";
  private CommonTokenStream tokens;

  @Override
  public Node visit(ParseTree tree) {
    if (tree == null) {
      return null;
    }
    Node node = super.visit(tree);
    if (log.isTraceEnabled()) {
      log.trace(
          "Visit {} -> {}",
          tree.getClass().getSimpleName(),
          node == null ? null : node.getClass().getSimpleName());
    }
    return node;
  }

  @Override
  public Node visitGraphql(GraphqlContext ctx) {
    return visit(ctx.document());
  }

  @Override
  public Document visitDocument(DocumentContext ctx) {
    Document.DocumentBuilder node = extract(ctx, Document.builder());
    ImmutableList.Builder<DocumentDefinition> definitions = ImmutableList.builder();
    for (DefinitionContext context : ctx.definition()) {
      definitions.add((DocumentDefinition) visit(context));
    }
    node.definitions(definitions.build());
    return node.build();
  }

  @Override
  public Document visitExecutableDocument(ExecutableDocumentContext ctx) {
    Document.DocumentBuilder node = extract(ctx, Document.builder());
    ImmutableList.Builder<DocumentDefinition> definitions = ImmutableList.builder();
    for (ExecutableDefinitionContext context : ctx.executableDefinition()) {
      definitions.add((DocumentDefinition) visit(context));
    }
    node.definitions(definitions.build());
    return node.build();
  }

  @Override
  public Document visitTypeSystemDocument(TypeSystemDocumentContext ctx) {
    Document.DocumentBuilder node = extract(ctx, Document.builder());
    ImmutableList.Builder<DocumentDefinition> definitions = ImmutableList.builder();
    for (TypeSystemDefinitionContext context : ctx.typeSystemDefinition()) {
      definitions.add((DocumentDefinition) visit(context));
    }
    node.definitions(definitions.build());
    return node.build();
  }

  // region TypeDefinition
  @Override
  public Node visitScalarTypeDefinition(ScalarTypeDefinitionContext ctx) {
    return extract(ctx, ScalarTypeDefinition.builder()).build();
  }

  @Override
  public Node visitObjectTypeDefinition(ObjectTypeDefinitionContext ctx) {
    return extract(ctx, ObjectTypeDefinition.builder()).build();
  }
  // endregion

  @Override
  public BypassNode<List<EnumValueDefinition>> visitEnumValuesDefinition(
      EnumValuesDefinitionContext ctx) {
    ImmutableList.Builder<EnumValueDefinition> list = ImmutableList.builder();
    if (ctx != null) {
      for (EnumValueDefinitionContext context : ctx.enumValueDefinition()) {
        list.add(visitEnumValueDefinition(context));
      }
    }
    return BypassNode.of(list.build());
  }

  @Override
  public EnumValueDefinition visitEnumValueDefinition(EnumValueDefinitionContext ctx) {
    return extract(ctx, EnumValueDefinition.builder()).build();
  }

  @Override
  public Node visitScalarTypeExtension(ScalarTypeExtensionContext ctx) {
    return extract(ctx, ScalarTypeExtension.builder()).build();
  }

  @Override
  public Node visitInterfaceTypeDefinition(InterfaceTypeDefinitionContext ctx) {
    return extract(ctx, InterfaceTypeDefinition.builder()).build();
  }

  @Override
  public Node visitSchemaDefinition(SchemaDefinitionContext ctx) {
    SchemaDefinition.SchemaDefinitionBuilder node = SchemaDefinition.builder();
    extract(ctx, node);
    for (OperationTypeDefinitionContext context : ctx.operationTypeDefinition()) {
      switch (context.operationType().getText()) {
        case "query":
          node.queryTypeName(context.namedType().getText());
          break;
        case "mutation":
          node.mutationTypeName(context.namedType().getText());
          break;
        case "subscription":
          node.subscriptionTypeName(context.namedType().getText());
          break;
        default:
          throw new GraphLanguageException("Unsupported operation type");
      }
    }
    return node.build();
  }

  @Override
  public Node visitEnumTypeDefinition(EnumTypeDefinitionContext ctx) {
    return extract(ctx, EnumTypeDefinition.builder()).build();
  }

  @Override
  public Node visitUnionTypeExtension(UnionTypeExtensionContext ctx) {
    UnionTypeExtensionBuilder builder = extract(ctx, UnionTypeExtension.builder());
    builder.unionMemberTypes(visitUnionMemberTypes(ctx.unionMemberTypes()).getValue());
    return builder.build();
  }

  @Override
  public Node visitUnionTypeDefinition(UnionTypeDefinitionContext ctx) {
    UnionTypeDefinitionBuilder builder = extract(ctx, UnionTypeDefinition.builder());
    builder.memberTypes(visitUnionMemberTypes(ctx.unionMemberTypes()).getValue());
    return builder.build();
  }

  @Override
  public BypassNode<List<String>> visitUnionMemberTypes(UnionMemberTypesContext ctx) {
    ImmutableList.Builder<String> list = ImmutableList.builder();
    UnionMemberTypesContext self = ctx;
    while (self != null) {
      list.add(self.namedType().getText());
      self = self.unionMemberTypes();
    }
    return BypassNode.of(list.build());
  }

  // region Type
  @Override
  public NamedType visitNamedType(NamedTypeContext ctx) {
    return extract(ctx, NamedType.builder()).build();
  }
  // endregion

  @Override
  public ListType visitListType(ListTypeContext ctx) {
    return extract(ctx, ListType.builder()).build();
  }

  @Override
  public NonNullType visitNonNullType(NonNullTypeContext ctx) {
    NonNullType.NonNullTypeBuilder builder = extract(ctx, NonNullType.builder());
    if (ctx.namedType() != null) {
      builder.type(visitNamedType(ctx.namedType()));
    } else {
      builder.type(visitListType(ctx.listType()));
    }
    return builder.build();
  }

  @Override
  public BypassNode<List<String>> visitImplementsInterfaces(ImplementsInterfacesContext ctx) {
    ImmutableList.Builder<String> list = ImmutableList.builder();
    ImplementsInterfacesContext self = ctx;
    while (self != null) {
      list.add(self.namedType().getText());
      self = self.implementsInterfaces();
    }
    return BypassNode.of(list.build());
  }

  @Override
  public BypassNode<List<FieldDefinition>> visitFieldsDefinition(FieldsDefinitionContext ctx) {
    ImmutableList.Builder<FieldDefinition> list = ImmutableList.builder();
    if (ctx != null) {
      for (FieldDefinitionContext context : ctx.fieldDefinition()) {
        list.add(visitFieldDefinition(context));
      }
    }
    return BypassNode.of(list.build());
  }

  @Override
  public FieldDefinition visitFieldDefinition(FieldDefinitionContext ctx) {
    return extract(ctx, FieldDefinition.builder()).build();
  }

  @Override
  public Node visitDirectiveDefinition(DirectiveDefinitionContext ctx) {
    return extract(ctx, DirectiveDefinition.builder())
        .locations(visitDirectiveLocations(ctx.directiveLocations()).getValue())
        .build();
  }

  @Override
  public BypassNode<List<Directive>> visitDirectives(DirectivesContext ctx) {
    ImmutableList.Builder<Directive> list = ImmutableList.builder();
    if (ctx != null) {
      for (DirectiveContext context : ctx.directive()) {
        list.add(visitDirective(context));
      }
    }
    return BypassNode.of(list.build());
  }

  @Override
  public Directive visitDirective(DirectiveContext ctx) {
    return extract(ctx, Directive.builder()).build();
  }

  @Override
  public BypassNode<List<Argument>> visitArguments(ArgumentsContext ctx) {
    ImmutableList.Builder<Argument> list = ImmutableList.builder();
    if (ctx != null) {
      for (ArgumentContext context : ctx.argument()) {
        list.add(visitArgument(context));
      }
    }
    return BypassNode.of(list.build());
  }

  @Override
  public Argument visitArgument(ArgumentContext ctx) {
    return extract(ctx, Argument.builder()).build();
  }

  @Override
  public BypassNode<List<String>> visitDirectiveLocations(DirectiveLocationsContext ctx) {
    DirectiveLocationsContext self = ctx;
    ImmutableList.Builder<String> locations = ImmutableList.builder();
    while (self != null) {
      locations.add(self.DirectiveLocation().getText());
      self = self.directiveLocations();
    }
    return BypassNode.of(locations.build());
  }

  @Override
  public BypassNode<List<InputValueDefinition>> visitArgumentsDefinition(
      ArgumentsDefinitionContext ctx) {
    ImmutableList.Builder<InputValueDefinition> list = ImmutableList.builder();
    if (ctx != null) {
      for (InputValueDefinitionContext context : ctx.inputValueDefinition()) {
        list.add(visitInputValueDefinition(context));
      }
    }

    return BypassNode.of(list.build());
  }

  @Override
  public InputValueDefinition visitInputValueDefinition(InputValueDefinitionContext ctx) {
    return extract(ctx, InputValueDefinition.builder()).build();
  }

  @Override
  public BypassNode<List<InputValueDefinition>> visitInputFieldsDefinition(
      InputFieldsDefinitionContext ctx) {
    ImmutableList.Builder<InputValueDefinition> list = ImmutableList.builder();
    if (ctx != null) {
      for (InputValueDefinitionContext context : ctx.inputValueDefinition()) {
        list.add(visitInputValueDefinition(context));
      }
    }

    return BypassNode.of(list.build());
  }

  @Override
  public Node visitObjectTypeExtension(ObjectTypeExtensionContext ctx) {
    return extract(ctx, ObjectTypeExtension.builder()).build();
  }

  @Override
  public Node visitEnumTypeExtension(EnumTypeExtensionContext ctx) {
    return extract(ctx, EnumTypeExtension.builder()).build();
  }

  @Override
  public Node visitInputObjectTypeDefinition(InputObjectTypeDefinitionContext ctx) {
    return extract(ctx, InputObjectTypeDefinition.builder()).build();
  }

  @Override
  public Node visitInputObjectTypeExtension(InputObjectTypeExtensionContext ctx) {
    return extract(ctx, InputObjectTypeExtension.builder()).build();
  }

  @Override
  public Node visitBooleanValue(BooleanValueContext ctx) {
    return extract(ctx, BooleanValue.builder()).value(Boolean.parseBoolean(ctx.getText())).build();
  }

  @Override
  public Node visitNullValue(NullValueContext ctx) {
    return extract(ctx, NullValue.builder()).build();
  }

  @Override
  public Node visitFloatValue(FloatValueContext ctx) {
    return extract(ctx, FloatValue.builder()).value(Float.parseFloat(ctx.getText())).build();
  }

  @Override
  public Node visitIntValue(IntValueContext ctx) {
    return extract(ctx, IntValue.builder()).value(Integer.parseInt(ctx.getText())).build();
  }

  @Override
  public Node visitListValue(ListValueContext ctx) {
    ImmutableList.Builder<Value> list = ImmutableList.builder();
    for (ValueContext context : ctx.value()) {
      list.add((Value) visit(context));
    }
    return extract(ctx, ListValue.builder()).value(list.build()).build();
  }

  @Override
  public Node visitObjectValue(ObjectValueContext ctx) {
    ImmutableMap.Builder<String, Value> map = ImmutableMap.builder();
    for (ObjectFieldContext context : ctx.objectField()) {
      map.put(visitObjectField(context).getValue());
    }
    return extract(ctx, ObjectValue.builder()).value(map.build()).build();
  }

  @Override
  public Node visitEnumValue(EnumValueContext ctx) {
    return extract(ctx, EnumValue.builder()).name(extractText(ctx.name())).build();
  }

  @Override
  public Node visitVariable(VariableContext ctx) {
    return extract(ctx, Variable.builder()).build();
  }

  // region execution

  @Override
  public Node visitStringValue(StringValueContext ctx) {
    return extract(ctx, StringValue.builder()).value(extractString(ctx)).build();
  }

  @Override
  public Node visitInterfaceTypeExtension(InterfaceTypeExtensionContext ctx) {
    return extract(ctx, InterfaceTypeExtension.builder()).build();
  }

  @Override
  public Node visitOperationDefinition(OperationDefinitionContext ctx) {
    OperationDefinition.OperationDefinitionBuilder node =
        extract(ctx, OperationDefinition.builder());
    node.operationType(extractText(ctx.operationType(), "query"));
    ImmutableList.Builder<VariableDefinition> variableDefinitions = ImmutableList.builder();
    if (ctx.variableDefinitions() != null) {
      for (VariableDefinitionContext context : ctx.variableDefinitions().variableDefinition()) {
        variableDefinitions.add(visitVariableDefinition(context));
      }
    }
    node.variableDefinitions(variableDefinitions.build());
    return node.build();
  }

  @Override
  public VariableDefinition visitVariableDefinition(VariableDefinitionContext ctx) {
    return extract(ctx, VariableDefinition.builder())
        .name(extractText(ctx.variable().name()))
        .build();
  }

  @Override
  public Node visitFragmentDefinition(FragmentDefinitionContext ctx) {
    return extract(ctx, FragmentDefinition.builder()).name(extractText(ctx.fragmentName())).build();
  }

  @Override
  public SelectionSet visitSelectionSet(SelectionSetContext ctx) {
    if (ctx == null) {
      return null;
    }
    SelectionSet.SelectionSetBuilder builder = extract(ctx, SelectionSet.builder());
    ImmutableList.Builder<Selection> selections = ImmutableList.builder();
    for (SelectionContext context : ctx.selection()) {
      selections.add((Selection) visit(context));
    }

    return builder.selections(selections.build()).build();
  }

  @Override
  public Field visitField(FieldContext ctx) {
    return extract(ctx, Field.builder())
        .alias(extractText(ctx.alias() == null ? null : ctx.alias().name()))
        .build();
  }

  // endregion

  @Override
  public FragmentSpread visitFragmentSpread(FragmentSpreadContext ctx) {
    return extract(ctx, FragmentSpread.builder())
        .fragmentName(extractText(ctx.fragmentName()))
        .build();
  }

  @Override
  public InlineFragment visitInlineFragment(InlineFragmentContext ctx) {
    return extract(ctx, InlineFragment.builder()).build();
  }

  @Override
  public BypassNode<Entry<String, Value>> visitObjectField(ObjectFieldContext ctx) {
    return BypassNode.of(new SimpleEntry<>(ctx.name().getText(), (Value) visit(ctx.value())));
  }

  private <T extends Builders.BuildNode> T extract(ParserRuleContext ctx, T builder) {
    if (ctx == null) {
      return builder;
    }
    if (builder == null) {
      throw new RuntimeException("require builder");
    }
    builder.sourceLocation(extractSourceLocation(ctx));
    builder.comments(extractComments(ctx));

    if (builder instanceof BuildArguments) {
      ((BuildArguments) builder)
          .arguments(visitArguments(ctx.getRuleContext(ArgumentsContext.class, 0)).getValue());
    }
    if (builder instanceof BuildArgumentDefinitions) {
      ((BuildArgumentDefinitions) builder)
          .argumentDefinitions(
              visitArgumentsDefinition(ctx.getRuleContext(ArgumentsDefinitionContext.class, 0))
                  .getValue());
    }

    if (builder instanceof BuildDefaultValue) {
      ((BuildDefaultValue) builder)
          .defaultValue((Value) visit(ctx.getRuleContext(DefaultValueContext.class, 0)));
    }
    if (builder instanceof BuildDescription) {
      ((BuildDescription) builder)
          .description(extractString(ctx.getRuleContext(DescriptionContext.class, 0)));
    }
    if (builder instanceof BuildDirectives) {
      ((BuildDirectives) builder)
          .directives(visitDirectives(ctx.getRuleContext(DirectivesContext.class, 0)).getValue());
    }
    if (builder instanceof BuildEnumValue) {
      ((BuildEnumValue) builder).name(extractText(ctx.getRuleContext(EnumValueContext.class, 0)));
    }
    if (builder instanceof BuildEnumValueDefinitions) {
      ((BuildEnumValueDefinitions) builder)
          .enumValueDefinitions(
              visitEnumValuesDefinition(ctx.getRuleContext(EnumValuesDefinitionContext.class, 0))
                  .getValue());
    }
    if (builder instanceof BuildFieldDefinitions) {
      ((BuildFieldDefinitions) builder)
          .fieldDefinitions(
              visitFieldsDefinition(ctx.getRuleContext(FieldsDefinitionContext.class, 0))
                  .getValue());
    }
    //    if (node instanceof HasFragmentName) {
    //      ((HasFragmentName) node)
    //          .setName(extractText(ctx.getRuleContext(FragmentNameContext.class, 0)));
    //    }
    if (builder instanceof BuildTypeCondition) {
      TypeConditionContext typeConditionContext = ctx.getRuleContext(TypeConditionContext.class, 0);
      if (typeConditionContext != null) {
        ((BuildTypeCondition) builder).typeCondition(extractText(typeConditionContext.namedType()));
      }
    }
    if (builder instanceof BuildInterfaces) {
      ((BuildInterfaces) builder)
          .interfaces(
              visitImplementsInterfaces(ctx.getRuleContext(ImplementsInterfacesContext.class, 0))
                  .getValue());
    }
    if (builder instanceof BuildInputFieldsDefinitions) {
      ((BuildInputFieldsDefinitions) builder)
          .inputFieldsDefinitions(
              visitInputFieldsDefinition(ctx.getRuleContext(InputFieldsDefinitionContext.class, 0))
                  .getValue());
    }
    if (builder instanceof BuildName) {
      ((Builders.BuildName) builder).name(extractText(ctx.getRuleContext(NameContext.class, 0)));
    }

    //    // extend NAME by EXTEND_TYPE_NAME
    //    if (builder instanceof BuildExtendTypeName) {
    //      // Must after HasName check
    //      ((BuildExtendTypeName) builder)
    //          .extendTypeName(extractText(ctx.getRuleContext(NameContext.class, 1)));
    //      //      ((BuildExtendTypeName)
    // builder).name(extractText(ctx.getRuleContext(NameContext.class,
    //      // 1)));
    //    }
    if (builder instanceof BuildTypeExtension) {
      // extend <ExtendTypeName> by <ExtendByName> as <Name>
      BuildTypeExtension<BuildTypeExtension<BuildTypeExtension>> b = (BuildTypeExtension) builder;
      List<NameContext> names = ctx.getRuleContexts(NameContext.class);
      switch (names.size()) {
        case 1:
          b.extendTypeName(extractText(names.get(0)));
          break;
        case 2:
          b.extendTypeName(extractText(names.get(0)));
          b.extendByName(extractText(names.get(1)));
          // TOOD support extend ? as ?
          break;
        case 3:
          b.extendTypeName(extractText(names.get(0)))
              .extendByName(extractText(names.get(1)))
              .name(extractText(names.get(2)));
          break;
      }

      //      ((Builders.BuildTypeExtension)
      // builder).name(extractText(ctx.getRuleContext(NameContext.class, 0)));
    }
    if (builder instanceof BuildSelectionSet) {
      ((BuildSelectionSet) builder)
          .selectionSet(visitSelectionSet(ctx.getRuleContext(SelectionSetContext.class, 0)));
    }
    if (builder instanceof BuildType) {
      ((BuildType) builder).type((Type) visit(ctx.getRuleContext(TypeContext.class, 0)));
    }
    if (builder instanceof BuildValue) {
      ((BuildValue) builder).value((Value) visit(ctx.getRuleContext(ValueContext.class, 0)));
    }
    return builder;
  }

  private String extractText(ParseTree node, String def) {
    String v = extractText(node);
    if (v == null) {
      return def;
    }
    return v;
  }

  private String extractText(ParseTree node) {
    if (node == null) {
      return null;
    }
    return node.getText();
  }

  private String extractString(ParseTree node) {
    if (node == null) {
      return null;
    }
    String text = node.getText();
    // TODO Escape
    return text.substring(1, text.length() - 1);
  }

  private List<Comment> extractComments(ParserRuleContext ctx) {
    Token start = ctx.getStart();
    if (start != null) {
      int tokPos = start.getTokenIndex();
      List<Token> refChannel = tokens.getHiddenTokensToLeft(tokPos, 2);
      if (refChannel != null) {
        return getCommentOnChannel(refChannel);
      }
    }
    return Collections.emptyList();
  }

  private List<Comment> getCommentOnChannel(List<Token> refChannel) {
    Builder<Comment> comments = ImmutableList.builder();
    for (Token refTok : refChannel) {
      String text = refTok.getText();
      // we strip the leading hash # character but we don't trim because we don't
      // know the "comment markup".  Maybe its space sensitive, maybe its not.  So
      // consumers can decide that
      if (text == null) {
        continue;
      }
      text = text.replaceFirst("^#", "");
      comments.add(
          commentBuilder.content(text).sourceLocation(extractSourceLocation(refTok)).build());
    }
    return comments.build();
  }

  private SourceLocation extractSourceLocation(ParserRuleContext ctx) {
    return extractSourceLocation(ctx.getStart());
  }

  private SourceLocation extractSourceLocation(Token token) {
    return sourceLocationBuilder
        .line(token.getLine())
        .column(token.getCharPositionInLine())
        .source(source)
        .build();
  }
}
