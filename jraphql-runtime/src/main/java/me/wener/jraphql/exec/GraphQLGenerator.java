package me.wener.jraphql.exec;

import java.util.List;
import java.util.Map;
import me.wener.jraphql.lang.Comment;
import me.wener.jraphql.lang.EnumValue;
import me.wener.jraphql.lang.InputValueDefinition;
import me.wener.jraphql.lang.ListValue;
import me.wener.jraphql.lang.Node;
import me.wener.jraphql.lang.ObjectValue;
import me.wener.jraphql.lang.StringValue;
import me.wener.jraphql.lang.Type;
import me.wener.jraphql.lang.Value;
import me.wener.jraphql.lang.Variable;

/**
 * Generate GraphQL from language node
 *
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/4
 */
public interface GraphQLGenerator {

  static CharSequence generate(List<? extends Node> v) {
    return generate(v, ",");
  }

  static CharSequence generate(List<? extends Node> v, CharSequence sp) {
    StringBuilder sb = new StringBuilder();
    v.forEach(i -> sb.append(generate(i)).append(sp));
    return sb;
  }

  static CharSequence generate(Node v) {
    if (v instanceof InputValueDefinition) {
      return generate((InputValueDefinition) v);
    }
    throw new UnsupportedOperationException("unsupported type " + v.getClass().getSimpleName());
  }

  static CharSequence generate(InputValueDefinition v) {
    StringBuilder sb = new StringBuilder();
    sb.append(v.getName()).append(':').append(generateType(v.getType()));
    if (v.getDefaultValue() != null) {
      sb.append('=').append(generateValue(v.getDefaultValue()));
    }
    return sb;
  }

  static CharSequence generateComments(List<Comment> v) {
    StringBuilder sb = new StringBuilder();
    v.forEach(i -> sb.append(generateComment(i)).append('\n'));
    return sb;
  }

  static CharSequence generateComment(Comment v) {
    return '#' + v.getContent();
  }

  //  static CharSequence generateScalarType(ScalarTypeDefinition v) {
  //    StringBuilder sb = new StringBuilder();
  //    if (!v.getComments().isEmpty()) {
  //      sb.append(generateComments(v.getComments()));
  //    }
  //    return sb;
  //  }

  static CharSequence generateString(String v) {
    return '"' + v + '"';
  }

  static CharSequence generateType(Type type) {
    switch (type.getKind()) {
      case NAMED:
        return type.getName();
      case NON_NULL:
        return generateType(type.getType()) + "!";
      case LIST:
        return "[" + generateType(type.getType()) + "]";
      default:
        throw new AssertionError();
    }
  }

  static CharSequence generateValue(Value value) {
    switch (value.getKind()) {
      case NULL:
      case FLOAT:
      case INT:
      case BOOLEAN:
        return String.valueOf(value.getValue());
      case ENUM:
        return value.unwrap(EnumValue.class).getName();
      case STRING:
        // TODO Escape
        return value.unwrap(StringValue.class).getValue();
      case OBJECT:
        {
          Map<String, Value> map = value.unwrap(ObjectValue.class).getValue();
          StringBuilder sb = new StringBuilder();
          sb.append('{');
          map.forEach((k, v) -> sb.append(k).append(':').append(generateValue(v)).append(','));
          sb.append('}');
          return sb;
        }
      case LIST:
        {
          List<Value> values = value.unwrap(ListValue.class).getValue();
          StringBuilder sb = new StringBuilder();
          sb.append('[');
          values.forEach(v -> sb.append(generateValue(v)).append(','));
          sb.append(']');
          return sb;
        }
      case VARIABLE:
        return "$" + value.unwrap(Variable.class).getName();
      default:
        throw new AssertionError();
    }
  }
}
