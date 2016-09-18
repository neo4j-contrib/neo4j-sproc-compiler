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
package net.biville.florent.sproccompiler;

import com.google.testing.compile.CompilationRule;
import com.google.testing.compile.CompileTester;
import org.junit.Rule;
import org.junit.Test;

import javax.annotation.processing.Processor;
import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static net.biville.florent.sproccompiler.testutils.JavaFileObjectUtils.resource;

public class ContextProcessorTest
{

    @Rule
    public CompilationRule compilation = new CompilationRule();

    private Processor processor = new ContextProcessor();

    @Test
    public void fails_if_context_injected_fields_have_wrong_modifiers()
    {
        JavaFileObject sproc = resource( "bad_context_field/BadContextSproc.java" );

        CompileTester.UnsuccessfulCompilationClause unsuccessfulCompilationClause =
                assert_().about( javaSource() ).that( sproc ).processedWith( processor ).failsToCompile()
                        .withErrorCount( 3 );

        unsuccessfulCompilationClause.withErrorContaining(
                "@org.neo4j.procedure.Context usage error: field BadContextSproc#shouldBeNonStatic should be public, non-static and non-final" )
                .in( sproc ).onLine( 25 );

        unsuccessfulCompilationClause.withErrorContaining(
                "@org.neo4j.procedure.Context usage error: field BadContextSproc#shouldBeNonFinal should be public, non-static and non-final" )
                .in( sproc ).onLine( 27 );

        unsuccessfulCompilationClause.withErrorContaining(
                "@org.neo4j.procedure.Context usage error: field BadContextSproc#shouldBePublic should be public, non-static and non-final" )
                .in( sproc ).onLine( 31 );
    }


}
