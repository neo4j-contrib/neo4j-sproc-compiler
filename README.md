# Neo4j stored procedure compiler

[![Build Status](https://travis-ci.org/fbiville/neo4j-sproc-compiler.png?branch=master)](https://travis-ci.org/fbiville/neo4j-sproc-compiler)

This is a annotation processor that will verify your stored procedures
at compile time.

While most of the basic checks can be performed, you still need
some unit tests to verify some runtime behaviours.

# Use the processor

## Maven

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

And add the dependency simply as follows:

```xml
<dependency>
   <groupId>net.biville.florent</groupId>
   <artifactId>neo4j-sproc-compiler</artifactId>
   <version>1.0-SNAPSHOT</version>
   <optional>true</optional>
</dependency>
```

## Gradle

Just add to your repositories:

```
maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
```

And to your dependencies:

```
compileOnly group: 'net.biville.florent', name: 'neo4j-sproc-compiler', version:'1.0-SNAPSHOT'
```
