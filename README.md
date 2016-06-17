# Neo4j stored procedure compiler

[![Build Status](https://travis-ci.org/fbiville/neo4j-sproc-compiler.png?branch=master)](https://travis-ci.org/fbiville/neo4j-sproc-compiler)

This is a annotation processor that will verify your stored procedures
at compile time.

While most of the basic checks can be performed, you still need
some unit tests to verify some runtime behaviours.

# Use the processor

## Maven

### SNAPSHOT repository

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

### Dependency

Add the dependency simply as follows:

```xml
<dependency>
   <groupId>net.biville.florent</groupId>
   <artifactId>neo4j-sproc-compiler</artifactId>
   <version><!-- check last release on https://search.maven.org --></version>
   <optional>true</optional>
</dependency>
```

## Gradle

### SNAPSHOT repository

> If you do not plan to test the development version, you can skip this section.

Just add to your repositories:

```
maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
```

### Dependency

Add to your dependencies:

```
compileOnly group: 'net.biville.florent', name: 'neo4j-sproc-compiler', version:'/* check last release on https://search.maven.org */'
```
