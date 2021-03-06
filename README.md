# JraphQL
Java with GraphQL

Module | Description
-------|-----------
jraphql-lang | GraphQL language representation
jraphql-runtime | GraphQL execution engine
jraphql-parser-antlr | Parser written in Antlr4 parse to jraphql-lang

<!-- tobe remove
~~jrapgql-api~~ | Minimal api that may required by others
~~jraphql-graphql-java-adapter~~ | Adapter for [graphql-java/graphql-java](https://github.com/graphql-java/graphql-java)
-->

## Get Started

* Maven [me.wener.jraphql](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22me.wener.jraphql%22)

```
<dependency>
  <groupId>me.wener.jraphql</groupId>
  <artifactId>jraphql-runtimer</artifactId>
  <version>0.0.6</version>
</dependency>
<dependency>
  <groupId>me.wener.jraphql</groupId>
  <artifactId>jraphql-parser-antlr</artifactId>
  <version>0.0.6</version>
</dependency>
```

## Features

### Language representation
Feature | Description
--------------------|----
__Serializable__    | can parse or stringify to or from JSON
__Immutable__       | friendly to cache or precompile
__Buildable__       | every type has a builder for it generated by lombok.
__Pluggable__       | language representation is not related to parser impl

### Syntax Extension

#### Add `extend by name` syntax for object and interface

#### Weave multi schemas
```graphql
# common.graphqls
scalar Version

# crm.graphqls
type CrmQuery {
  customer(id:ID!):Customer
  crmVersion: Version!
}
type CrmUser {
  customers: [Customer]
}
extend type Query by CrmQuery
extend type User by CrmUser

# erp.graphqls
type ErpQuery {
  product(id:ID!):Product
}
extend type Query by ErpQuery
```

#### Conditional schema

```graphql
# Only admin can see and use these methods
type AdminMutation {
  changePassword(id:ID,password:String): ChangePasswordPayload
}
extend type Mutation by AdminMutation @Role(role:"admin")
```

#### Allowed directives on directive definition, add DIRECTIVE location

```graphql
directive @JavaType(type:String) on DIRECTIVE
directive @Auth(value:String) @JavaType(type:"Auth") on FIELD_DEFINITION;
```

#### Allowed schema has optional name

```graphql
schema Test {
  query: MyQuery
}
```

### Runtime Extension

1. Type implements interface don't need to write the fields again.

```graphql
interface Node {
    id: ID!
}

type User implements Node {
    # id: ID! # This is optional
}
``` 

2. Can disable introspection
    * `new MetaResolver().setDisableIntrospection(true)`

### Embeddable Schema

JraphQL Runtime contain a embedded schema [MetaSchema](./jraphql-runtime/src/main/java/me/wener/jraphql/schema/MetaSchema.java), generated by [EmbededSchema](./jraphql-runtime/src/test/java/me/wener/jraphql/example/EmbededSchema.java).   

* Parse Schema
* Serialize to JSON
* Best compress GZip
* Encode use mime base64
* Original JSON 32631 byte -> Encoded Base64 5352 byte

## Example

### StarWar

* [StarWarApplication](./jraphql-runtime/src/test/java/me/wener/jraphql/example/StarWarApplication.java)
    * SpringBoot WebFlux based
    * start and visite http://localhost:8080
* [StarWarResolver](./jraphql-runtime/src/test/java/me/wener/jraphql/example/StarWarResolverV1.java)
    * resolve the StarWar schema. 

Queries you can try

```graphql
mutation addRev {
  createReview(episode: EMPIRE, review: {stars: 4, commentary: "Ok Good"}) {
    stars
    commentary
  }
}

query rev($e:Episode = EMPIRE) {
  hero(episode: $e) {
    id
    name
    appearsIn
  }
  reviews(episode: $e) {
    stars
    commentary
  }
}

query search {
  search(text: "o") {
    __typename
    ... on Human {
      id
      name
    }
    ... on Droid {
      primaryFunction
    }
    ... on Starship {
      length
    }
  }
}

query baseQuery {
  starship(id: "3000") {
    id
    name
    length(unit: FOOT)
  }
  character(id: "2000") {
    id
    ... on Human {
      mass
      starships {
        name
      }
    }
    ... on Droid {
      name
      appearsIn
    }
  }
  human(id: "1003") {
    friendsConnection(after: "1002") {
      friends {
        name
      }
      pageInfo {
        hasNextPage
        startCursor
        endCursor
      }
    }
    friends {
      name
    }
  }
}
```

## Work with GoaphQL
[GoaphQL](https://github.com/wenerme/goaphql) can generate code from schema that depends on jrapgql-api, can directly run on jraphql-graphql-java-adapter.  

The generated code is static type and full featured, everything is an interface.
