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
package net.biville.florent.sproccompiler.visitors.examples;

import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

/*
 * see also BadUserFunction in root package
 */
public class UserFunctionsExamples
{
    @UserFunction( name = "in_root_namespace" )
    public String functionWithName()
    {
        return "42";
    }

    @UserFunction( value = "in_root_namespace_again" )
    public String functionWithValue()
    {
        return "42";
    }

    @UserFunction( name = "not.in.root.namespace" )
    public String ok()
    {
        return "42";
    }

    @UserFunction( name = "com.acme.foobar" )
    public void wrongReturnType()
    {

    }

    @UserFunction( name = "com.acme.foobar" )
    public String wrongParameterType( @Name( "foo" ) Thread foo )
    {
        return "42";
    }

    @UserFunction( name = "com.acme.foobar" )
    public String missingParameterAnnotation( @Name( "foo" ) String foo, String oops )
    {
        return "42";
    }
}
