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

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

public class BadPrimitiveInputUserFunction
{

    @UserFunction
    public String doSomething( @Name( "test" ) short unsupportedType )
    {
        return "42";
    }

    @UserFunction
    public String works01( @Name( "test" ) String supported )
    {
        return "42";
    }

    @UserFunction
    public String works02( @Name( "test" ) Long supported )
    {
        return "42";
    }

    @UserFunction
    public String works03( @Name( "test" ) long supported )
    {
        return "42";
    }

    @UserFunction
    public String works04( @Name( "test" ) Double supported )
    {
        return "42";
    }

    @UserFunction
    public String works05( @Name( "test" ) double supported )
    {
        return "42";
    }

    @UserFunction
    public String works06( @Name( "test" ) Number supported )
    {
        return "42";
    }

    @UserFunction
    public String works07( @Name( "test" ) Boolean supported )
    {
        return "42";
    }

    @UserFunction
    public String works08( @Name( "test" ) boolean supported )
    {
        return "42";
    }

    @UserFunction
    public String works09( @Name( "test" ) Object supported )
    {
        return "42";
    }

    @UserFunction
    public String works10( @Name( "test" ) Node supported )
    {
        return "42";
    }

    @UserFunction
    public String works11( @Name( "test" ) Relationship supported )
    {
        return "42";
    }

    @UserFunction
    public String works12( @Name( "test" ) Path supported )
    {
        return "42";
    }
}
