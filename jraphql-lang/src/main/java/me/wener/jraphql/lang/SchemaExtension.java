//package me.wener.jraphql.lang;
//
//import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
//import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
//import java.util.Collections;
//import java.util.List;
//import lombok.Builder;
//import lombok.NonNull;
//import lombok.Value;
//
///**
// * @author <a href=http://github.com/wenerme>wener</a>
// * @since 09/04/2018
// */
//@Value
//@Builder(toBuilder = true)
//@JsonDeserialize(builder = SchemaExtension.SchemaExtensionBuilder.class)
//public class SchemaExtension implements TypeExtension {
//
//  @NonNull private SourceLocation sourceLocation;
//  @NonNull @Builder.Default private List<Comment> comments = Collections.emptyList();
//  private String name;
//  @NonNull private String extendTypeName;
//  private String extendByName;
//  private String description;
//  @NonNull @Builder.Default private List<Directive> directives = Collections.emptyList();
//
//  private String queryTypeName;
//  private String mutationTypeName;
//  private String subscriptionTypeName;
//
//
//  public static class SchemaExtensionBuilder
//      implements Builders.BuildTypeDefinition<SchemaExtensionBuilder> {}
//}
