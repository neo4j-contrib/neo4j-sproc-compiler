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

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

public class UserFunctions
{

    @UserFunction
    public String simpleInput00()
    {
        return "42";
    }

    @UserFunction
    public String simpleInput01( @Name( "foo" ) String input )
    {
        return "42";
    }

    @UserFunction
    public String simpleInput02( @Name( "foo" ) long input )
    {
        return "42";
    }

    @UserFunction
    public String simpleInput03( @Name( "foo" ) Long input )
    {
        return "42";
    }

    @UserFunction
    public String simpleInput04( @Name( "foo" ) Number input )
    {
        return "42";
    }

    @UserFunction
    public String simpleInput05( @Name( "foo" ) Boolean input )
    {
        return "42";
    }

    @UserFunction
    public String simpleInput06( @Name( "foo" ) boolean input )
    {
        return "42";
    }

    @UserFunction
    public String simpleInput07( @Name( "foo" ) Object input )
    {
        return "42";
    }

    @UserFunction
    public String simpleInput08( @Name( "foo" ) Node input )
    {
        return "42";
    }

    @UserFunction
    public String simpleInput09( @Name( "foo" ) Path input )
    {
        return "42";
    }

    @UserFunction
    public String simpleInput10( @Name( "foo" ) Relationship input )
    {
        return "42";
    }

    @UserFunction
    public String genericInput01( @Name( "foo" ) List<String> input )
    {
        return "42";
    }

    @UserFunction
    public String genericInput02( @Name( "foo" ) List<List<Node>> input )
    {
        return "42";
    }

    @UserFunction
    public String genericInput03( @Name( "foo" ) Map<String,List<Node>> input )
    {
        return "42";
    }

    @UserFunction
    public String genericInput04( @Name( "foo" ) Map<String,Object> input )
    {
        return "42";
    }

    @UserFunction
    public String genericInput05( @Name( "foo" ) Map<String,List<List<Map<String,Map<String,List<Path>>>>>> input )
    {
        return "42";
    }

}
