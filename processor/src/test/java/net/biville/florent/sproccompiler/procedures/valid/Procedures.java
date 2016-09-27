/*
 * Copyright 2016-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.biville.florent.sproccompiler.procedures.valid;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.Procedure;

public class Procedures
{

    @Procedure
    public Stream<Records.LongWrapper> theAnswer()
    {
        return Stream.of( new Records.LongWrapper( 42L ) );
    }

    @Procedure
    public void simpleInput00()
    {
    }

    @Procedure
    public void simpleInput01( @Name( "foo" ) String input )
    {
    }

    @Procedure
    public void simpleInput02( @Name( "foo" ) long input )
    {
    }

    @Procedure
    public void simpleInput03( @Name( "foo" ) Long input )
    {
    }

    @Procedure
    public void simpleInput04( @Name( "foo" ) Number input )
    {
    }

    @Procedure
    public void simpleInput05( @Name( "foo" ) Boolean input )
    {
    }

    @Procedure
    public void simpleInput06( @Name( "foo" ) boolean input )
    {
    }

    @Procedure
    public void simpleInput07( @Name( "foo" ) Object input )
    {
    }

    @Procedure
    public void simpleInput08( @Name( "foo" ) Node input )
    {
    }

    @Procedure
    public void simpleInput09( @Name( "foo" ) Path input )
    {
    }

    @Procedure
    public void simpleInput10( @Name( "foo" ) Relationship input )
    {
    }

    @Procedure
    public Stream<Records.SimpleTypesWrapper> simpleInput11( @Name( "foo" ) String input )
    {
        return Stream.of( new Records.SimpleTypesWrapper() );
    }

    @Procedure
    public Stream<Records.SimpleTypesWrapper> simpleInput12( @Name( "foo" ) long input )
    {
        return Stream.of( new Records.SimpleTypesWrapper() );
    }

    @Procedure
    public Stream<Records.SimpleTypesWrapper> simpleInput13( @Name( "foo" ) Long input )
    {
        return Stream.of( new Records.SimpleTypesWrapper() );
    }

    @Procedure
    public Stream<Records.SimpleTypesWrapper> simpleInput14( @Name( "foo" ) Number input )
    {
        return Stream.of( new Records.SimpleTypesWrapper() );
    }

    @Procedure
    public Stream<Records.SimpleTypesWrapper> simpleInput15( @Name( "foo" ) Boolean input )
    {
        return Stream.of( new Records.SimpleTypesWrapper() );
    }

    @Procedure
    public Stream<Records.SimpleTypesWrapper> simpleInput16( @Name( "foo" ) boolean input )
    {
        return Stream.of( new Records.SimpleTypesWrapper() );
    }

    @Procedure
    public Stream<Records.SimpleTypesWrapper> simpleInput17( @Name( "foo" ) Object input )
    {
        return Stream.of( new Records.SimpleTypesWrapper() );
    }

    @Procedure
    public Stream<Records.SimpleTypesWrapper> simpleInput18( @Name( "foo" ) Node input )
    {
        return Stream.of( new Records.SimpleTypesWrapper() );
    }

    @Procedure
    public Stream<Records.SimpleTypesWrapper> simpleInput19( @Name( "foo" ) Path input )
    {
        return Stream.of( new Records.SimpleTypesWrapper() );
    }

    @Procedure
    public Stream<Records.SimpleTypesWrapper> simpleInput20( @Name( "foo" ) Relationship input )
    {
        return Stream.of( new Records.SimpleTypesWrapper() );
    }

    @Procedure
    public Stream<Records.SimpleTypesWrapper> simpleInput21()
    {
        return Stream.of( new Records.SimpleTypesWrapper() );
    }

    @Procedure
    public void genericInput01( @Name( "foo" ) List<String> input )
    {
    }

    @Procedure
    public void genericInput02( @Name( "foo" ) List<List<Node>> input )
    {
    }

    @Procedure
    public void genericInput03( @Name( "foo" ) Map<String,List<Node>> input )
    {
    }

    @Procedure
    public void genericInput04( @Name( "foo" ) Map<String,Object> input )
    {
    }

    @Procedure
    public void genericInput05( @Name( "foo" ) Map<String,List<List<Map<String,Map<String,List<Path>>>>>> input )
    {
    }

    @Procedure
    public Stream<Records.GenericTypesWrapper> genericInput06( @Name( "foo" ) List<String> input )
    {
        return Stream.of( new Records.GenericTypesWrapper() );
    }

    @Procedure
    public Stream<Records.GenericTypesWrapper> genericInput07( @Name( "foo" ) List<List<Node>> input )
    {
        return Stream.of( new Records.GenericTypesWrapper() );
    }

    @Procedure
    public Stream<Records.GenericTypesWrapper> genericInput08( @Name( "foo" ) Map<String,List<Node>> input )
    {
        return Stream.of( new Records.GenericTypesWrapper() );
    }

    @Procedure
    public Stream<Records.GenericTypesWrapper> genericInput09( @Name( "foo" ) Map<String,Object> input )
    {
        return Stream.of( new Records.GenericTypesWrapper() );
    }

    @Procedure
    public Stream<Records.GenericTypesWrapper> genericInput10(
            @Name( "foo" ) Map<String,List<List<Map<String,Map<String,List<Path>>>>>> input )
    {
        return Stream.of( new Records.GenericTypesWrapper() );
    }
}
