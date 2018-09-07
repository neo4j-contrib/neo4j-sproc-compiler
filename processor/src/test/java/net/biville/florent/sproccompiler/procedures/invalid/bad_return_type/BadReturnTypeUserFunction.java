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
package net.biville.florent.sproccompiler.procedures.invalid.bad_return_type;

import java.util.stream.Stream;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

public class BadReturnTypeUserFunction
{

    @Context
    public GraphDatabaseService db;

    @UserFunction
    public Stream<Long> wrongReturnTypeFunction( @Name( "foo" ) String parameter )
    {
        return Stream.empty();
    }

    @UserFunction
    public Long niceFunction( @Name( "foo" ) String parameter )
    {
        return 3L;
    }
}
