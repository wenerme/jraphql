# JraphQL
Java with GraphQL

* Maven [me.wener.jraphql](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22me.wener.jraphql%22)

Module | Description
-------|-----------
jrapgql-api | Minimal api that may required by others
jraphql-lang | GraphQL language representation
jraphql-parser-antlr | Parser written by Antlr4 parse to jraphql-lang
jraphql-runtime | Runtime required to execute GraphQL __[WIP]__
jraphql-graphql-java-adapter | Adapter for [graphql-java/graphql-java](https://github.com/graphql-java/graphql-java)



## Work with GoaphQL
[GoaphQL](https://github.com/wenerme/goaphql) can generate code from schema that depends on jrapgql-api, can directly run on jraphql-graphql-java-adapter.  

The generated code is static type and full featured, everything is an interface.
