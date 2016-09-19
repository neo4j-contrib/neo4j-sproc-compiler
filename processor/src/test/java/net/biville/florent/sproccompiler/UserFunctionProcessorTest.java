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
import com.google.testing.compile.CompileTester.UnsuccessfulCompilationClause;
import net.biville.florent.sproccompiler.testutils.JavaFileObjectUtils;
import org.junit.Rule;
import org.junit.Test;

import javax.annotation.processing.Processor;
import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Arrays.asList;

public class UserFunctionProcessorTest
{

    @Rule
    public CompilationRule compilation = new CompilationRule();

    private Processor processor = new UserFunctionProcessor();

    @Test
    public void fails_if_parameters_are_not_properly_annotated()
    {
        JavaFileObject function =
                JavaFileObjectUtils.INSTANCE.procedureSource( "invalid/missing_name/MissingNameUserFunction.java" );

        UnsuccessfulCompilationClause compilation =
                assert_().about( javaSource() ).that( function ).processedWith( processor ).failsToCompile()
                        .withErrorCount( 2 );

        compilation.withErrorContaining( "@org.neo4j.procedure.Name usage error: missing on parameter <parameter>" )
                .in( function ).onLine( 24 );

        compilation.withErrorContaining( "@org.neo4j.procedure.Name usage error: missing on parameter <otherParam>" )
                .in( function ).onLine( 24 );
    }

    @Test
    public void fails_if_return_type_is_incorrect()
    {
        JavaFileObject function = JavaFileObjectUtils.INSTANCE
                .procedureSource( "invalid/bad_return_type/BadReturnTypeUserFunction.java" );

        assert_().about( javaSource() ).that( function ).processedWith( processor ).failsToCompile().withErrorCount( 1 )
                .withErrorContaining(
                        "Unsupported return type <java.util.stream.Stream<java.lang.Long>> of function defined in <net.biville.florent.sproccompiler.procedures.invalid.bad_return_type.BadReturnTypeUserFunction#wrongReturnTypeFunction>" )
                .in( function ).onLine( 32 );
    }


    @Test
    public void fails_if_function_primitive_input_type_is_not_supported()
    {
        JavaFileObject function = JavaFileObjectUtils.INSTANCE
                .procedureSource( "invalid/bad_proc_input_type/BadPrimitiveInputUserFunction.java" );

        assert_().about( javaSource() ).that( function ).processedWith( processor ).failsToCompile().withErrorCount( 1 )
                .withErrorContaining(
                        "Unsupported parameter type <short> of procedure|function BadPrimitiveInputUserFunction#doSomething" )
                .in( function ).onLine( 28 );
    }

    @Test
    public void fails_if_function_generic_input_type_is_not_supported()
    {
        JavaFileObject function = JavaFileObjectUtils.INSTANCE
                .procedureSource( "invalid/bad_proc_input_type/BadGenericInputUserFunction.java" );

        UnsuccessfulCompilationClause compilation =
                assert_().about( javaSource() ).that( function ).processedWith( processor ).failsToCompile()
                        .withErrorCount( 3 );

        compilation.withErrorContaining( "Unsupported parameter type " +
                "<java.util.List<java.util.List<java.util.Map<java.lang.String,java.lang.Thread>>>>" +
                " of procedure|function BadGenericInputUserFunction#doSomething" ).in( function ).onLine( 32 );

        compilation.withErrorContaining( "Unsupported parameter type " +
                "<java.util.Map<java.lang.String,java.util.List<java.util.concurrent.ExecutorService>>>" +
                " of procedure|function BadGenericInputUserFunction#doSomething2" ).in( function ).onLine( 38 );

        compilation.withErrorContaining(
                "Unsupported parameter type <java.util.Map> of procedure|function BadGenericInputUserFunction#doSomething3" )
                .in( function ).onLine( 44 );
    }


    @Test
    public void fails_if_duplicate_functions_are_declared()
    {
        JavaFileObject firstDuplicate =
                JavaFileObjectUtils.INSTANCE.procedureSource( "invalid/duplicated/UserFunction1.java" );
        JavaFileObject secondDuplicate =
                JavaFileObjectUtils.INSTANCE.procedureSource( "invalid/duplicated/UserFunction2.java" );

        assert_().about( javaSources() ).that( asList( firstDuplicate, secondDuplicate ) ).processedWith( processor )
                .failsToCompile().withErrorCount( 2 ).withErrorContaining(
                "Procedure|function name <net.biville.florent.sproccompiler.procedures.invalid.duplicated.foobar> is already defined 2 times. " +
                        "It should be defined only once!" );
    }

    @Test
    public void succeeds_to_process_valid_stored_procedures()
    {
        assert_().about( javaSource() )
                .that( JavaFileObjectUtils.INSTANCE.procedureSource( "valid/UserFunctions.java" ) )
                .processedWith( processor ).compilesWithoutError();

    }
}
