package me.wener.jraphql.exec;

import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import me.wener.jraphql.lang.Definition;
import me.wener.jraphql.lang.DirectiveDefinition;
import me.wener.jraphql.lang.Document;
import me.wener.jraphql.lang.DocumentDefinition;
import me.wener.jraphql.lang.FieldDefinition;
import me.wener.jraphql.lang.GraphLanguageException;
import me.wener.jraphql.lang.Langs;
import me.wener.jraphql.lang.NamedType;
import me.wener.jraphql.lang.ObjectTypeDefinition;
import me.wener.jraphql.lang.SchemaDefinition;
import me.wener.jraphql.lang.Type;
import me.wener.jraphql.lang.TypeDefinition;
import me.wener.jraphql.lang.TypeExtension;
import me.wener.jraphql.schema.MetaSchema;

/**
 * Represent a full schema with merged extensions
 *
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/4
 */
@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder(toBuilder = true)
public class TypeSystemDocument {

  private static final String META_QUERY_NAME = "__MetaQuery";
  @NonNull private Map<String, TypeDefinition> definitions;
  @NonNull private Table<String, String, FieldDefinition> fields;
  @NonNull private ObjectTypeDefinition queryTypeDefinition;
  @NonNull private ListMultimap<Class<?>, DocumentDefinition> instances;
  private ObjectTypeDefinition mutationTypeDefinition;
  private ObjectTypeDefinition subscriptionTypeDefinition;

  public static TypeSystemDocumentBuilder builder() {
    return new TypeSystemDocumentBuilderPrebuild();
  }

  static void checkValueType(Type type, Object value) {
    if (type.getKind().isNonnull()) {
      if (value == null) {
        throw new GraphLanguageException("null for nonnull variable");
      }
      checkValueType(type.getType(), value);
      return;
    }
    if (value != null && type.getKind().isList()) {
      if (!(value instanceof Iterable)) {
        throw new GraphLanguageException("list need a iterable value");
      }
      for (Object o : (Iterable) (value)) {
        checkValueType(type.getType(), value);
      }
      return;
    }
    if (value != null && type.getKind().isNamed()) {
      switch (type.unwrap(NamedType.class).getName()) {
        case "Int":
          if (!(value instanceof Long)) {
            throw new GraphLanguageException(
                "Int type expected but found " + value.getClass().getSimpleName());
          }
          break;
        case "Float":
          if (!(value instanceof Double)) {
            throw new GraphLanguageException(
                "Float type expected but found " + value.getClass().getSimpleName());
          }
          break;
        case "String":
          if (!(value instanceof String)) {
            throw new GraphLanguageException(
                "String type expected but found " + value.getClass().getSimpleName());
          }
          break;
        case "Boolean":
          if (!(value instanceof Boolean)) {
            throw new GraphLanguageException(
                "Boolean type expected but found " + value.getClass().getSimpleName());
          }
          break;
      }
      // TODO
    }
  }

  //  public me.wener.jraphql.exec.TypeKind getTypeKind(String typeName) {
  //    TypeDefinition definition = definitions.get(typeName);
  //    switch (definition.getKind()) {
  //      case SCALAR:
  //        return me.wener.jraphql.exec.TypeKind.SCALAR;
  //      case ENUM:
  //        return me.wener.jraphql.exec.TypeKind.ENUM;
  //      case OBJECT:
  //        return me.wener.jraphql.exec.TypeKind.OBJECT;
  //      case INTERFACE:
  //        return me.wener.jraphql.exec.TypeKind.INTERFACE;
  //      case UNION:
  //        return me.wener.jraphql.exec.TypeKind.UNION;
  //      case INPUT_OBJECT:
  //        return me.wener.jraphql.exec.TypeKind.INPUT_OBJECT;
  //    }
  //    throw new AssertionError();
  //  }
  //
  //  public me.wener.jraphql.exec.TypeKind getTypeKind(Type type) {
  //    switch (type.getKind()) {
  //      case NAMED:
  //        return getTypeKind(type.getName());
  //      case NON_NULL:
  //        return me.wener.jraphql.exec.TypeKind.NON_NULL;
  //      case LIST:
  //        return me.wener.jraphql.exec.TypeKind.LIST;
  //    }
  //    throw new AssertionError();
  //  }

  public List<DirectiveDefinition> getDirectives() {
    return getAll(DirectiveDefinition.class);
  }

  <T> List<T> getAll(Class<T> clazz) {
    return (List<T>) instances.get(clazz);
  }

  public ObjectTypeDefinition getOperationTypeDefinition(String type) {
    switch (type) {
      case "query":
        return queryTypeDefinition;
      case "mutation":
        return mutationTypeDefinition;
      case "subscription":
        return subscriptionTypeDefinition;
    }
    return null;
  }

  public Optional<TypeDefinition> getDefinition(String name) {
    return Optional.ofNullable(definitions.get(name));
  }

  public TypeDefinition requireDefinition(String name) {
    TypeDefinition definition = definitions.get(name);
    if (definition == null) {
      throw new GraphLanguageException(String.format("type '%s' not found", name));
    }
    return definition;
  }

  public FieldDefinition _getFieldDefinition(String typeName, String fieldName) {
    FieldDefinition definition = fields.get(typeName, fieldName);
    if (definition == null) {
      switch (fieldName) {
        case "__typename":
          definition = fields.get(META_QUERY_NAME, fieldName);
          break;
        case "__schema":
        case "__type":
          // For root query only
          if (!typeName.equals(queryTypeDefinition.getName())) {
            break;
          }
          definition = fields.get(META_QUERY_NAME, fieldName);
          break;
      }
    }
    return definition;
  }

  public Optional<FieldDefinition> getFieldDefinition(String typeName, String fieldName) {
    return Optional.ofNullable(_getFieldDefinition(typeName, fieldName));
  }

  public FieldDefinition requireFieldDefinition(String typeName, String fieldName) {
    FieldDefinition fieldDefinition = _getFieldDefinition(typeName, fieldName);
    if (fieldDefinition == null) {
      throw new GraphLanguageException(
          String.format("field '%s.%s' not found", typeName, fieldName));
    }
    return fieldDefinition;
  }

  public Introspection.Schema generateIntrospection() {
    return new IntrospectionBuilder(this).getSchema();
  }

  @Accessors(chain = true, fluent = true)
  public static class TypeSystemDocumentBuilder {
    protected final List<DocumentDefinition> documentDefinitions = Lists.newArrayList();

    @Setter(AccessLevel.PRIVATE)
    protected ListMultimap<Class<?>, DocumentDefinition> instances;

    @Setter(AccessLevel.PRIVATE)
    protected Map<String, TypeDefinition> definitions;

    @Setter(AccessLevel.PRIVATE)
    protected Table<String, String, FieldDefinition> fields;

    @Setter(AccessLevel.PRIVATE)
    protected ObjectTypeDefinition queryTypeDefinition;

    @Setter(AccessLevel.PRIVATE)
    protected ObjectTypeDefinition mutationTypeDefinition;

    @Setter(AccessLevel.PRIVATE)
    protected ObjectTypeDefinition subscriptionTypeDefinition;

    @Setter(AccessLevel.PRIVATE)
    protected String schemaName;

    public TypeSystemDocumentBuilder addMetaDocument() {
      addDocument(MetaSchema.getDocument());
      return this;
    }

    public TypeSystemDocumentBuilder addDocument(Document... documents) {
      for (Document document : documents) {
        documentDefinitions.addAll(document.getDefinitions());
      }
      return this;
    }
  }

  private static class TypeSystemDocumentBuilderPrebuild extends TypeSystemDocumentBuilder {

    @Override
    public TypeSystemDocument build() {
      prebuild();
      return super.build();
    }

    void prebuild() {
      definitions = Maps.newHashMap();

      ListMultimap<TypeDefinition, TypeExtension> extensions = LinkedListMultimap.create();

      // indexing
      for (DocumentDefinition definition : documentDefinitions) {
        if (!Langs.isTypeDefinition(definition)) {
          continue;
        }
        String name = definition.getName();
        TypeDefinition old = definitions.put(name, (TypeDefinition) definition);
        if (old != null) {
          throw new GraphLanguageException(
              String.format(
                  "duplicate type definition of %s(%s) previous definition %s(%s)",
                  name,
                  definition.getSourceLocation().toLocation(),
                  old.getNodeTypeName(),
                  old.getSourceLocation().toLocation()));
        }
      }

      // find extension
      for (Definition definition : documentDefinitions) {
        if (Langs.isTypeExtension(definition)) {
          TypeExtension extension = (TypeExtension) definition;
          TypeDefinition type = definitions.get(extension.getExtendTypeName());

          if (type == null) {
            throw new GraphLanguageException(
                String.format("extend type '%s' not found", extension.getExtendTypeName()));
          }

          if (extension.getKind() != type.getKind()) {
            throw new GraphLanguageException(
                String.format(
                    "extension kind '%s' not match type kind '%s'",
                    extension.getKind(), type.getKind()));
          }

          extensions.put(type, extension);
        }
      }

      Map<String, TypeDefinition> extended = Maps.newHashMap();
      // do extension
      for (Entry<TypeDefinition, Collection<TypeExtension>> entry : extensions.asMap().entrySet()) {
        TypeDefinition type = entry.getKey();
        Collection<TypeExtension> exts = entry.getValue();

        TypeDefinition e = TypeExtender.extend(type, exts, definitions);
        extended.put(e.getName(), e);
      }
      //      definitions.put(extended.getName(), extended);
      definitions.putAll(extended);

      // category
      instances = LinkedListMultimap.create();
      definitions.values().forEach(v -> instances.put(v.getClass(), v));

      // find schema
      SchemaDefinition schemaDefinition = null;
      List<DocumentDefinition> schemas = instances.get(SchemaDefinition.class);
      if (schemas.isEmpty()) {
        throw new GraphLanguageException("No schema found");
      }

      if (!Strings.isNullOrEmpty(schemaName)) {
        for (DocumentDefinition schema : schemas) {
          if (Objects.equals(schema.getName(), schemaName)) {
            schemaDefinition = schema.unwrap(SchemaDefinition.class);
          }
        }
        if (schemaDefinition == null) {
          throw new GraphLanguageException(String.format("schema '%s' not found", schemaName));
        }
      } else {
        for (DocumentDefinition schema : schemas) {
          if (schema.getName() == null) {
            schemaDefinition = schema.unwrap(SchemaDefinition.class);
          }
        }
        if (schemaDefinition == null) {
          if (schemas.size() == 1) {
            schemaDefinition = schemas.get(0).unwrap(SchemaDefinition.class);
          } else {
            throw new GraphLanguageException(String.format("found %s schema", schemas.size()));
          }
        }
      }

      // find operations
      {
        TypeDefinition definition = definitions.get(schemaDefinition.getQueryTypeName());
        if (definition == null) {
          throw new GraphLanguageException(
              String.format("query '%s' not found", schemaDefinition.getQueryTypeName()));
        }
        queryTypeDefinition = definition.unwrap(ObjectTypeDefinition.class);
      }

      if (!Strings.isNullOrEmpty(schemaDefinition.getMutationTypeName())) {
        TypeDefinition definition = definitions.get(schemaDefinition.getMutationTypeName());
        if (definition == null) {
          throw new GraphLanguageException(
              String.format("mutation '%s' not found", schemaDefinition.getMutationTypeName()));
        }
        mutationTypeDefinition = definition.unwrap(ObjectTypeDefinition.class);
      }

      String typeName = schemaDefinition.getSubscriptionTypeName();
      if (!Strings.isNullOrEmpty(typeName)) {
        TypeDefinition definition = definitions.get(typeName);
        if (definition == null) {
          throw new GraphLanguageException(String.format("subscription '%s' not found", typeName));
        }
        subscriptionTypeDefinition = definition.unwrap(ObjectTypeDefinition.class);
      }

      // index fields
      fields = HashBasedTable.create();

      for (DocumentDefinition documentDefinition : instances.get(ObjectTypeDefinition.class)) {
        ObjectTypeDefinition objectTypeDefinition =
            documentDefinition.unwrap(ObjectTypeDefinition.class);
        for (FieldDefinition fieldDefinition : objectTypeDefinition.getFieldDefinitions()) {
          fields.put(objectTypeDefinition.getName(), fieldDefinition.getName(), fieldDefinition);
        }
      }

      // add meta field to root query
      //      FieldDefinition schemaField =
      //          FieldDefinition.builder()
      //              .sourceLocation(Langs.runtimeLocation())
      //              .name("__schema")
      //              .type(
      //                  NamedType.builder()
      //                      .sourceLocation(Langs.runtimeLocation())
      //                      .name("__Schema")
      //                      .build())
      //              .build();
      //      fields.put(queryTypeDefinition.getName(), "__schema", schemaField);

      // introspection
      //      Introspection.Schema.builder()
    }
  }

}
