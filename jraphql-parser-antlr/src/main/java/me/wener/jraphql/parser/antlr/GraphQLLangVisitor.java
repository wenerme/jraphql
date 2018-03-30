package me.wener.jraphql.parser.antlr;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import lombok.extern.slf4j.Slf4j;
import me.wener.jraphql.lang.Argument;
import me.wener.jraphql.lang.Definition;
import me.wener.jraphql.lang.Directive;
import me.wener.jraphql.lang.DirectiveDefinition;
import me.wener.jraphql.lang.Document;
import me.wener.jraphql.lang.EnumTypeDefinition;
import me.wener.jraphql.lang.EnumTypeExtension;
import me.wener.jraphql.lang.EnumValueDefinition;
import me.wener.jraphql.lang.Field;
import me.wener.jraphql.lang.FieldDefinition;
import me.wener.jraphql.lang.FragmentDefinition;
import me.wener.jraphql.lang.FragmentSpread;
import me.wener.jraphql.lang.GraphLanguageException;
import me.wener.jraphql.lang.HasArgumentDefinitions;
import me.wener.jraphql.lang.HasArguments;
import me.wener.jraphql.lang.HasDefaultValue;
import me.wener.jraphql.lang.HasDescription;
import me.wener.jraphql.lang.HasDirectives;
import me.wener.jraphql.lang.HasEnumValue;
import me.wener.jraphql.lang.HasEnumValueDefinitions;
import me.wener.jraphql.lang.HasExtendTypeName;
import me.wener.jraphql.lang.HasFieldDefinitions;
import me.wener.jraphql.lang.HasFragmentName;
import me.wener.jraphql.lang.HasInterfaces;
import me.wener.jraphql.lang.HasName;
import me.wener.jraphql.lang.HasSelectionSet;
import me.wener.jraphql.lang.HasType;
import me.wener.jraphql.lang.HasTypeCondition;
import me.wener.jraphql.lang.HasValue;
import me.wener.jraphql.lang.InlineFragment;
import me.wener.jraphql.lang.InputObjectTypeDefinition;
import me.wener.jraphql.lang.InputObjectTypeExtension;
import me.wener.jraphql.lang.InputValueDefinition;
import me.wener.jraphql.lang.InterfaceTypeDefinition;
import me.wener.jraphql.lang.InterfaceTypeExtension;
import me.wener.jraphql.lang.Langs;
import me.wener.jraphql.lang.ListType;
import me.wener.jraphql.lang.NamedType;
import me.wener.jraphql.lang.Node;
import me.wener.jraphql.lang.NonNullType;
import me.wener.jraphql.lang.NullValue;
import me.wener.jraphql.lang.ObjectTypeDefinition;
import me.wener.jraphql.lang.ObjectTypeExtension;
import me.wener.jraphql.lang.OperationDefinition;
import me.wener.jraphql.lang.ScalarTypeDefinition;
import me.wener.jraphql.lang.ScalarTypeExtension;
import me.wener.jraphql.lang.SchemaDefinition;
import me.wener.jraphql.lang.Selection;
import me.wener.jraphql.lang.SelectionSet;
import me.wener.jraphql.lang.SourceLocation;
import me.wener.jraphql.lang.Type;
import me.wener.jraphql.lang.Value;
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
import me.wener.jraphql.parser.antlr.GraphQLParser.FieldContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.FieldDefinitionContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.FieldsDefinitionContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.FloatValueContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.FragmentDefinitionContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.FragmentNameContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.FragmentSpreadContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.GraphqlContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.ImplementsInterfacesContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.InlineFragmentContext;
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
import me.wener.jraphql.parser.antlr.GraphQLParser.UnionMemberTypesContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.ValueContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.VariableContext;
import me.wener.jraphql.parser.antlr.GraphQLParser.VariableDefinitionContext;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
@Slf4j
public class GraphQLLangVisitor extends me.wener.jraphql.parser.antlr.GraphQLBaseVisitor<Node> {

  @Override
  public Node visit(ParseTree tree) {
    if (tree == null) {
      return null;
    }
    Node node = super.visit(tree);
    log.debug(
      "Visit {} -> {}",
      tree.getClass().getSimpleName(),
      node == null ? null : node.getClass().getSimpleName());
    return node;
  }

  @Override
  public Node visitGraphql(GraphqlContext ctx) {
    return visit(ctx.document());
  }

  @Override
  public Document visitDocument(DocumentContext ctx) {
    Document node = extract(ctx, new Document());
    List<Definition> definitions = Lists.newArrayList();
    for (DefinitionContext context : ctx.definition()) {
      definitions.add((Definition) visit(context));
    }
    node.setDefinitions(definitions);
    return node;
  }

  // region TypeDefinition
  @Override
  public Node visitScalarTypeDefinition(ScalarTypeDefinitionContext ctx) {
    return extract(ctx, new ScalarTypeDefinition());
  }

  @Override
  public Node visitObjectTypeDefinition(ObjectTypeDefinitionContext ctx) {
    return extract(ctx, new ObjectTypeDefinition());
  }

  @Override
  public BypassNode<List<EnumValueDefinition>> visitEnumValuesDefinition(
    EnumValuesDefinitionContext ctx) {
    List<EnumValueDefinition> list = Lists.newArrayList();
    if (ctx != null) {
      for (EnumValueDefinitionContext context : ctx.enumValueDefinition()) {
        list.add(visitEnumValueDefinition(context));
      }
    }
    return BypassNode.of(list);
  }

  @Override
  public EnumValueDefinition visitEnumValueDefinition(EnumValueDefinitionContext ctx) {
    return extract(ctx, new EnumValueDefinition());
  }
  // endregion

  @Override
  public Node visitScalarTypeExtension(ScalarTypeExtensionContext ctx) {
    return extract(ctx, new ScalarTypeExtension());
  }

  @Override
  public Node visitInterfaceTypeDefinition(InterfaceTypeDefinitionContext ctx) {
    return extract(ctx, new InterfaceTypeDefinition());
  }

  @Override
  public Node visitSchemaDefinition(SchemaDefinitionContext ctx) {
    SchemaDefinition node = new SchemaDefinition();
    extract(ctx, node);
    for (OperationTypeDefinitionContext context : ctx.operationTypeDefinition()) {
      switch (context.operationType().getText()) {
        case "query":
          node.setQueryTypeName(context.namedType().getText());
          break;
        case "mutation":
          node.setMutationTypeName(context.namedType().getText());
          break;
        case "subscription":
          node.setSubscriptionTypeName(context.namedType().getText());
          break;
        default:
          throw new GraphLanguageException("Unsupported operation type");
      }
    }
    return node;
  }

  @Override
  public Node visitEnumTypeDefinition(EnumTypeDefinitionContext ctx) {
    return extract(ctx, new EnumTypeDefinition());
  }

  @Override
  public BypassNode<List<String>> visitUnionMemberTypes(UnionMemberTypesContext ctx) {
    List<String> list = Lists.newArrayList();
    UnionMemberTypesContext self = ctx;
    while (self != null) {
      list.add(self.namedType().getText());
      self = self.unionMemberTypes();
    }
    return BypassNode.of(list);
  }

  // region Type
  @Override
  public NamedType visitNamedType(NamedTypeContext ctx) {
    return extract(ctx, new NamedType());
  }

  @Override
  public ListType visitListType(ListTypeContext ctx) {
    return extract(ctx, new ListType());
  }

  @Override
  public NonNullType visitNonNullType(NonNullTypeContext ctx) {
    NonNullType node = new NonNullType();
    if (ctx.namedType() != null) {
      node.setType(visitNamedType(ctx.namedType()));
    } else {
      node.setType(visitListType(ctx.listType()));
    }
    return node;
  }
  // endregion

  @Override
  public BypassNode<List<String>> visitImplementsInterfaces(ImplementsInterfacesContext ctx) {
    List<String> list = Lists.newArrayList();
    ImplementsInterfacesContext self = ctx;
    while (self != null) {
      list.add(self.namedType().getText());
      self = self.implementsInterfaces();
    }
    return BypassNode.of(list);
  }

  @Override
  public BypassNode<List<FieldDefinition>> visitFieldsDefinition(FieldsDefinitionContext ctx) {
    List<FieldDefinition> list = Lists.newArrayList();
    if (ctx != null) {
      for (FieldDefinitionContext context : ctx.fieldDefinition()) {
        list.add(visitFieldDefinition(context));
      }
    }
    return BypassNode.of(list);
  }

  @Override
  public FieldDefinition visitFieldDefinition(FieldDefinitionContext ctx) {
    FieldDefinition node = new FieldDefinition();
    extract(ctx, node);
    return node;
  }

  @Override
  public Node visitDirectiveDefinition(DirectiveDefinitionContext ctx) {
    DirectiveDefinition node = new DirectiveDefinition();
    extract(ctx, node);
    node.setLocations(visitDirectiveLocations(ctx.directiveLocations()).getValue());
    return node;
  }

  @Override
  public BypassNode<List<Directive>> visitDirectives(DirectivesContext ctx) {
    List<Directive> list = Lists.newArrayList();
    if (ctx != null) {
      for (DirectiveContext context : ctx.directive()) {
        list.add(visitDirective(context));
      }
    }
    return BypassNode.of(list);
  }

  @Override
  public Directive visitDirective(DirectiveContext ctx) {
    return extract(ctx, new Directive());
  }

  @Override
  public BypassNode<List<Argument>> visitArguments(ArgumentsContext ctx) {
    List<Argument> list = Lists.newArrayList();
    if (ctx != null) {
      for (ArgumentContext context : ctx.argument()) {
        list.add(visitArgument(context));
      }
    }
    return BypassNode.of(list);
  }

  @Override
  public Argument visitArgument(ArgumentContext ctx) {
    return extract(ctx, new Argument());
  }

  @Override
  public BypassNode<List<String>> visitDirectiveLocations(DirectiveLocationsContext ctx) {
    DirectiveLocationsContext self = ctx;
    List<String> locations = Lists.newArrayList();
    while (self != null) {
      locations.add(self.DirectiveLocation().getText());
      self = self.directiveLocations();
    }
    return BypassNode.of(locations);
  }

  @Override
  public BypassNode<List<InputValueDefinition>> visitArgumentsDefinition(
    ArgumentsDefinitionContext ctx) {
    List<InputValueDefinition> list = Lists.newArrayList();
    if (ctx != null) {
      for (InputValueDefinitionContext context : ctx.inputValueDefinition()) {
        list.add(visitInputValueDefinition(context));
      }
    }

    return BypassNode.of(list);
  }

  @Override
  public InputValueDefinition visitInputValueDefinition(InputValueDefinitionContext ctx) {
    return extract(ctx, new InputValueDefinition());
  }

  @Override
  public Node visitObjectTypeExtension(ObjectTypeExtensionContext ctx) {
    return extract(ctx, new ObjectTypeExtension());
  }

  @Override
  public Node visitEnumTypeExtension(EnumTypeExtensionContext ctx) {
    return extract(ctx, new EnumTypeExtension());
  }

  @Override
  public Node visitInputObjectTypeDefinition(InputObjectTypeDefinitionContext ctx) {
    return extract(ctx, new InputObjectTypeDefinition());
  }

  @Override
  public Node visitInputObjectTypeExtension(InputObjectTypeExtensionContext ctx) {
    return extract(ctx, new InputObjectTypeExtension());
  }

  @Override
  public Node visitBooleanValue(BooleanValueContext ctx) {
    return extract(ctx, Langs.valueOf(Boolean.parseBoolean(ctx.getText())));
  }

  @Override
  public Node visitNullValue(NullValueContext ctx) {
    return extract(ctx, new NullValue());
  }

  @Override
  public Node visitFloatValue(FloatValueContext ctx) {
    return extract(ctx, Langs.valueOf(Double.parseDouble(ctx.getText())));
  }

  @Override
  public Node visitIntValue(IntValueContext ctx) {
    return extract(ctx, Langs.valueOf(Long.parseLong(ctx.getText())));
  }

  @Override
  public Node visitListValue(ListValueContext ctx) {
    List<Value> list = Lists.newArrayList();
    for (ValueContext context : ctx.value()) {
      list.add((Value) visit(context));
    }
    return extract(ctx, Langs.valueOf(list));
  }

  @Override
  public Node visitObjectValue(ObjectValueContext ctx) {
    HashMap<String, Value> map = Maps.newHashMap();
    for (ObjectFieldContext context : ctx.objectField()) {
      map.entrySet().add(visitObjectField(context).getValue());
    }
    return extract(ctx, Langs.valueOf(map));
  }

  @Override
  public Node visitEnumValue(EnumValueContext ctx) {
    return extract(ctx, Langs.enumOf(extractText(ctx)));
  }

  @Override
  public Node visitVariable(VariableContext ctx) {
    return extract(ctx, Langs.variableOf(extractText(ctx.name())));
  }

  @Override
  public Node visitInterfaceTypeExtension(InterfaceTypeExtensionContext ctx) {
    return extract(ctx, new InterfaceTypeExtension());
  }

  @Override
  public Node visitStringValue(StringValueContext ctx) {
    return extract(ctx, Langs.valueOf(extractString(ctx)));
  }

  // region execution

  @Override
  public Node visitOperationDefinition(OperationDefinitionContext ctx) {
    OperationDefinition node = extract(ctx, new OperationDefinition());
    node.setOperationType(extractText(ctx.operationType()));
    List<VariableDefinition> variableDefinitions = Lists.newArrayList();
    if (ctx.variableDefinitions() != null) {
      for (VariableDefinitionContext context : ctx.variableDefinitions().variableDefinition()) {
        variableDefinitions.add(visitVariableDefinition(context));
      }
    }
    node.setVariableDefinitions(variableDefinitions);
    return node;
  }

  @Override
  public VariableDefinition visitVariableDefinition(VariableDefinitionContext ctx) {
    VariableDefinition node = extract(ctx, new VariableDefinition());
    node.setVariableName(extractText(ctx.variable()));
    return node;
  }

  @Override
  public Node visitFragmentDefinition(FragmentDefinitionContext ctx) {
    FragmentDefinition node = extract(ctx, new FragmentDefinition());
    node.setFragmentName(extractText(ctx.fragmentName()));
    if (ctx.typeCondition() != null) {
      node.setTypeCondition(extractText(ctx.typeCondition()));
    }
    return node;
  }

  @Override
  public SelectionSet visitSelectionSet(SelectionSetContext ctx) {
    SelectionSet node = extract(ctx, new SelectionSet());
    List<Selection> selections = Lists.newArrayList();
    if (ctx != null) {
      for (SelectionContext context : ctx.selection()) {
        selections.add((Selection) visit(context));
      }
    }

    node.setSelections(selections);
    return node;
  }

  @Override
  public Node visitField(FieldContext ctx) {
    Field node = extract(ctx, new Field());
    node.setAlias(extractText(ctx.alias()));
    return node;
  }

  @Override
  public Node visitFragmentSpread(FragmentSpreadContext ctx) {
    return extract(ctx, new FragmentSpread());
  }

  @Override
  public Node visitInlineFragment(InlineFragmentContext ctx) {
    return extract(ctx, new InlineFragment());
  }

  // endregion

  @Override
  public BypassNode<Entry<String, Value>> visitObjectField(ObjectFieldContext ctx) {
    return BypassNode.of(new SimpleEntry<>(ctx.name().getText(), (Value) visit(ctx.value())));
  }

  private <T extends Node> T extract(ParserRuleContext ctx, T node) {
    if (ctx == null) {
      return node;
    }
    if (node == null) {
      throw new RuntimeException("require node");
    }
    node.setSourceLocation(
      new SourceLocation()
        .setLine(ctx.getStart().getLine())
        .setColumn(ctx.getStart().getCharPositionInLine()));

    // TODO comments

    if (node instanceof HasArguments) {
      ((HasArguments) node)
        .setArguments(visitArguments(ctx.getRuleContext(ArgumentsContext.class, 0)).getValue());
    }
    if (node instanceof HasArgumentDefinitions) {
      ((HasArgumentDefinitions) node)
        .setArgumentDefinitions(
          visitArgumentsDefinition(ctx.getRuleContext(ArgumentsDefinitionContext.class, 0))
            .getValue());
    }

    if (node instanceof HasDefaultValue) {
      ((HasDefaultValue) node)
        .setDefaultValue((Value) visit(ctx.getRuleContext(DefaultValueContext.class, 0)));
    }
    if (node instanceof HasDescription) {
      ((HasDescription) node)
        .setDescription(extractString(ctx.getRuleContext(DescriptionContext.class, 0)));
    }
    if (node instanceof HasDirectives) {
      ((HasDirectives) node)
        .setDirectives(
          visitDirectives(ctx.getRuleContext(DirectivesContext.class, 0)).getValue());
    }
    if (node instanceof HasEnumValue) {
      ((HasEnumValue) node)
        .setEnumValue(extractText(ctx.getRuleContext(EnumValueContext.class, 0)));
    }
    if (node instanceof HasEnumValueDefinitions) {
      ((HasEnumValueDefinitions) node)
        .setEnumValueDefinitions(
          visitEnumValuesDefinition(ctx.getRuleContext(EnumValuesDefinitionContext.class, 0))
            .getValue());
    }
    if (node instanceof HasFieldDefinitions) {
      ((HasFieldDefinitions) node)
        .setFieldDefinitions(
          visitFieldsDefinition(ctx.getRuleContext(FieldsDefinitionContext.class, 0))
            .getValue());
    }
    if (node instanceof HasFragmentName) {
      ((HasFragmentName) node)
        .setFragmentName(extractText(ctx.getRuleContext(FragmentNameContext.class, 0)));
    }
    if (node instanceof HasTypeCondition) {
      ((HasTypeCondition) node)
        .setTypeCondition(extractText(ctx.getRuleContext(TypeConditionContext.class, 0)));
    }
    if (node instanceof HasInterfaces) {
      ((HasInterfaces) node)
        .setInterfaces(
          visitImplementsInterfaces(ctx.getRuleContext(ImplementsInterfacesContext.class, 0))
            .getValue());
    }
    if (node instanceof HasName) {
      ((HasName) node).setName(extractText(ctx.getRuleContext(NameContext.class, 0)));
    }

    // extend NAME by EXTEND_TYPE_NAME
    if (node instanceof HasExtendTypeName) {
      // Must after HasName check
      ((HasExtendTypeName) node).setExtendTypeName(((HasName) node).getName());
      ((HasName) node).setName(extractText(ctx.getRuleContext(NameContext.class, 1)));
    }
    if (node instanceof HasSelectionSet) {
      ((HasSelectionSet) node)
        .setSelectionSet(visitSelectionSet(ctx.getRuleContext(SelectionSetContext.class, 0)));
    }
    if (node instanceof HasType) {
      ((HasType) node).setType((Type) visit(ctx.getRuleContext(TypeContext.class, 0)));
    }
    if (node instanceof HasValue) {
      ((HasValue) node).setValue((Value) visit(ctx.getRuleContext(TypeContext.class, 0)));
    }
    return node;
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
}
