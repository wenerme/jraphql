package me.wener.jraphql.lang;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A node of the abstract syntax tree
 *
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 16/03/2018
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "_type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = Argument.class, name = "Argument"),
  @JsonSubTypes.Type(value = BooleanValue.class, name = "BooleanValue"),
  @JsonSubTypes.Type(value = Directive.class, name = "Directive"),
  @JsonSubTypes.Type(value = DirectiveDefinition.class, name = "DirectiveDefinition"),
  @JsonSubTypes.Type(value = Document.class, name = "Document"),
  @JsonSubTypes.Type(value = EnumTypeDefinition.class, name = "EnumTypeDefinition"),
  @JsonSubTypes.Type(value = EnumTypeExtension.class, name = "EnumTypeExtension"),
  @JsonSubTypes.Type(value = EnumValue.class, name = "EnumValue"),
  @JsonSubTypes.Type(value = EnumValueDefinition.class, name = "EnumValueDefinition"),
  @JsonSubTypes.Type(value = Field.class, name = "Field"),
  @JsonSubTypes.Type(value = FieldDefinition.class, name = "FieldDefinition"),
  @JsonSubTypes.Type(value = FloatValue.class, name = "FloatValue"),
  @JsonSubTypes.Type(value = FragmentDefinition.class, name = "FragmentDefinition"),
  @JsonSubTypes.Type(value = FragmentSpread.class, name = "FragmentSpread"),
  @JsonSubTypes.Type(value = InlineFragment.class, name = "InlineFragment"),
  @JsonSubTypes.Type(value = InputObjectTypeDefinition.class, name = "InputObjectTypeDefinition"),
  @JsonSubTypes.Type(value = InputObjectTypeExtension.class, name = "InputObjectTypeExtension"),
  @JsonSubTypes.Type(value = InputValueDefinition.class, name = "InputValueDefinition"),
  @JsonSubTypes.Type(value = InterfaceTypeDefinition.class, name = "InterfaceTypeDefinition"),
  @JsonSubTypes.Type(value = InterfaceTypeExtension.class, name = "InterfaceTypeExtension"),
  @JsonSubTypes.Type(value = IntValue.class, name = "IntValue"),
  @JsonSubTypes.Type(value = ListType.class, name = "ListType"),
  @JsonSubTypes.Type(value = ListValue.class, name = "ListValue"),
  @JsonSubTypes.Type(value = NamedType.class, name = "NamedType"),
  @JsonSubTypes.Type(value = NonNullType.class, name = "NonNullType"),
  @JsonSubTypes.Type(value = NullValue.class, name = "NullValue"),
  @JsonSubTypes.Type(value = ObjectTypeDefinition.class, name = "ObjectTypeDefinition"),
  @JsonSubTypes.Type(value = ObjectTypeExtension.class, name = "ObjectTypeExtension"),
  @JsonSubTypes.Type(value = ObjectValue.class, name = "ObjectValue"),
  @JsonSubTypes.Type(value = OperationDefinition.class, name = "OperationDefinition"),
  @JsonSubTypes.Type(value = ScalarTypeDefinition.class, name = "ScalarTypeDefinition"),
  @JsonSubTypes.Type(value = ScalarTypeExtension.class, name = "ScalarTypeExtension"),
  @JsonSubTypes.Type(value = SchemaDefinition.class, name = "SchemaDefinition"),
  @JsonSubTypes.Type(value = SelectionSet.class, name = "SelectionSet"),
  @JsonSubTypes.Type(value = StringValue.class, name = "StringValue"),
  @JsonSubTypes.Type(value = UnionTypeDefinition.class, name = "UnionTypeDefinition"),
  @JsonSubTypes.Type(value = UnionTypeExtension.class, name = "UnionTypeExtension"),
  @JsonSubTypes.Type(value = Variable.class, name = "Variable"),
  @JsonSubTypes.Type(value = VariableDefinition.class, name = "VariableDefinition"),
})
@JsonInclude(Include.NON_EMPTY)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public interface Node extends Serializable {

  @Nonnull
  SourceLocation getSourceLocation();

  @Nonnull
  List<Comment> getComments();

  /**
   * Node may contain a name
   *
   * @return name of the node
   */
  @Nullable
  default String getName() {
    return null;
  }

  default String getNodeTypeName() {
    return getClass().getSimpleName();
  }

  @SuppressWarnings("unchecked")
  default <T> T unwrap(java.lang.Class<T> iface) {
    return (T) this;
  }
}
