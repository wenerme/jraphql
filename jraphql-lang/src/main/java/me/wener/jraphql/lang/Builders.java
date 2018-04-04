package me.wener.jraphql.lang;

import java.util.List;

/**
 * Builder abilities
 *
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/4
 */
public interface Builders {

  /** Builder used to build node of language */
  interface Builder {}

  interface BuildNode<T> extends Builder {
    T sourceLocation(SourceLocation sourceLocation);

    T comments(List<Comment> comments);
  }

  interface BuildDefinition<T> extends BuildNode<T> {}

  interface BuildTypeExtension<T>
      extends BuildNode<T>, BuildDirectives<T>, BuildExtendTypeName<T> {}

  interface BuildTypeDefinition<T>
      extends BuildNode<T>, BuildDirectives<T>, BuildDescription<T>, BuildName<T> {}

  interface BuildType<T> {
    T type(Type type);
  }

  interface BuildName<T> {
    T name(String name);
  }

  interface BuildArgumentDefinitions<T> {

    T argumentDefinitions(List<InputValueDefinition> s);
  }

  interface BuildArguments<T> {

    T arguments(List<Argument> arguments);
  }

  interface BuildDefaultValue<T> {

    T defaultValue(Value value);
  }

  interface BuildDescription<T> {

    T description(String s);
  }

  interface BuildDirectives<T> {

    T directives(List<Directive> s);
  }

  interface BuildEnumValue<T> {

    T enumValue(String s);
  }

  interface BuildEnumValueDefinitions<T> {

    T enumValueDefinitions(List<EnumValueDefinition> enumValueDefinitions);
  }

  interface BuildExtendTypeName<T> extends BuildName<T> {

    T extendTypeName(String s);
  }

  interface BuildFieldDefinitions<T> {

    T fieldDefinitions(List<FieldDefinition> fieldDefinitions);
  }

  interface BuildInterfaces<T> {

    T interfaces(List<String> interfaces);
  }

  interface BuildSelectionSet<T> {

    T selectionSet(SelectionSet selectionSet);
  }

  interface BuildTypeCondition<T> {

    T typeCondition(String typeCondition);
  }

  interface BuildValue<T> {

    T value(Value value);
  }
}
