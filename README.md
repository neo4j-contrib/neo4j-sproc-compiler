# Neo4j stored procedure compiler

[![Build Status](https://travis-ci.org/fbiville/neo4j-sproc-compiler.png?branch=master)](https://travis-ci.org/fbiville/neo4j-sproc-compiler)

This is a annotation processor that will verify your stored procedures
at compile time.

While most of the basic checks can be performed, you still need
some unit tests to verify some runtime behaviours.


## What does it do?

Once the stored procedure compiler is added into your project classpath (see Maven/Gradle
instructions below), it will trigger compilation failures if any of the following requirements
is not met:

 - `@Context` fields must be `public` and non-`final`
 - all other fields must be `static`
 - `Map` record fields/procedure parameters must define their key type as `String`
 - `@Procedure`|`@UserFunction` class must define a public constructor with no arguments
 - `@Procedure` method must return a Stream
 - `@Procedure`|`@UserFunction` parameter and record types must be supported
 - `@Procedure`|`@UserFunction` parameters must be annotated with `@Name`
 - `@UserFunction` cannot be defined in the root namespace
 - all visited `@Procedure`|`@UserFunction` names must be unique*

*A deployed Neo4j instance can aggregate stored procedures from different JARs.
Inter-JAR naming conflict cannot be detected by an annotation processor.
By definition, it can only inspect one compilation unit at a time.

## DSV export

The compiler can also export metadata to DSV (Delimiter-Separated Values) files.
To make it work, you need to specify the following options (by passing `-A${CONFIGURATION_KEY}=${value}` to the Java compiler (where `${CONFIGURATION_KEY}` is one of the keys listed below):

 - `GeneratedDocumentationPath`: mandatory - the folder path in which the files are going to be generated in. If not specified, the export won't be done.
 - `Documentation.FieldDelimiter`: optional (default: ,) - the delimiter of values within a row
 - `Documentation.ExportGrouping`: optional (default: SINGLE) - comma-separated values of grouping strategy. The data is exported to a single place (SINGLE), to a single place per enclosing package (PACKAGE), to a single place per enclosing class (CLASS). For now, "place" may mean one or two files, depending on `Documentation.ExportSplit`.
 - `Documentation.ExportSplit`: optional (default: NONE) - whether to split data by kind (KIND, e.g. procedure vs. function) or not (NONE).
 - `Documentation.ExportedHeaders`: optional (default: *) - delimiter-separated values which define custom ordering and filtering of the available headers. They are separated by the configured delimiter (see `Documentation.FieldDelimiter`). The available headers are (in default order):

     - type: `'procedure'` or `'function'` for now
     - name: procedure/function logical name
     - description: optional procedure/function description
     - execution mode: configured execution mode
     - location: fully qualified method name defining the procedure/function
     - deprecated by: optional replacement of the procedure/function
 
## Use the processor

### Maven

#### SNAPSHOT repository

> If you do not plan to test the development version, you can skip this section.

Add to `<repositories>` section:

```xml
   <repository>
      <id>sonatype-snapshot-repo</id>
      <name>Sonatype SNAPSHOT repository</name>
      <layout>default</layout>
      <releases>
         <enabled>false</enabled>
      </releases>
      <snapshots>
         <enabled>true</enabled>
      </snapshots>
      <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
   </repository>
```

#### Dependency

Add the dependency simply as follows:

```xml
<dependency>
   <groupId>net.biville.florent</groupId>
   <artifactId>neo4j-sproc-compiler</artifactId>
   <version><!-- check last release on https://search.maven.org --></version>
   <scope>provided</scope>
   <optional>true</optional>
</dependency>
```

### Gradle

#### SNAPSHOT repository

> If you do not plan to test the development version, you can skip this section.

Just add to your repositories:

```
maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
```

#### Dependency

Add to your dependencies:

```
compileOnly group: 'net.biville.florent', name: 'neo4j-sproc-compiler', version:'/* check last release on https://search.maven.org */'
```
