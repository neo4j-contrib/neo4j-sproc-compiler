/*
 * Copyright 2016-2018 the original author or authors.
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
package net.biville.florent.sproccompiler.procedures.invalid.bad_proc_input_type;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

public class BadGenericInputUserFunction
{

    @UserFunction
    public String doSomething( @Name( "test" ) List<List<Map<String,Thread>>> unsupportedType )
    {
        return "42";
    }

    @UserFunction
    public String doSomething2( @Name( "test" ) Map<String,List<ExecutorService>> unsupportedType )
    {
        return "42";
    }

    @UserFunction
    public String doSomething3( @Name( "test" ) Map unsupportedType )
    {
        return "42";
    }

    @UserFunction
    public String works1( @Name( "test" ) List<String> supported )
    {
        return "42";
    }

    @UserFunction
    public String works2( @Name( "test" ) List<List<Object>> supported )
    {
        return "42";
    }

    @UserFunction
    public String works3( @Name( "test" ) Map<String,Object> supported )
    {
        return "42";
    }

    @UserFunction
    public String works4( @Name( "test" ) List<List<List<Map<String,Object>>>> supported )
    {
        return "42";
    }

    @UserFunction
    public String works5( @Name( "test" ) List<List<List<Path>>> supported )
    {
        return "42";
    }

    @UserFunction
    public String works6( @Name( "test" ) List<Node> supported )
    {
        return "42";
    }

    @UserFunction
    public String works7( @Name( "test" ) List<List<Relationship>> supported )
    {
        return "42";
    }

    @UserFunction
    public String works8( @Name( "test" ) Map<String,List<List<Relationship>>> supported )
    {
        return "42";
    }

    @UserFunction
    public String works9( @Name( "test" ) Map<String,Map<String,List<Node>>> supported )
    {
        return "42";
    }
}
