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
import static com.google.testing.compile.JavaFileObjects.forResource;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Arrays.asList;

public class StoredProcedureProcessorTest
{

    @Rule
    public CompilationRule compilation = new CompilationRule();

    private Processor processor = new StoredProcedureProcessor();

    @Test
    public void fails_if_parameters_are_not_properly_annotated()
    {
        JavaFileObject sproc = JavaFileObjectUtils.resource( "missing_name/MissingNameSproc.java" );

        UnsuccessfulCompilationClause compilation =
                assert_().about( javaSource() ).that( sproc ).processedWith( processor ).failsToCompile()
                        .withErrorCount( 2 );

        compilation.withErrorContaining( "@org.neo4j.procedure.Name usage error: missing on parameter <parameter>" )
                .in( sproc ).onLine( 31 );

        compilation.withErrorContaining( "@org.neo4j.procedure.Name usage error: missing on parameter <otherParam>" )
                .in( sproc ).onLine( 31 );
    }

    @Test
    public void fails_if_return_type_is_not_stream()
    {
        JavaFileObject sproc = JavaFileObjectUtils.resource( "bad_return_type/BadReturnTypeSproc.java" );

        assert_().about( javaSource() ).that( sproc ).processedWith( processor ).failsToCompile().withErrorCount( 1 )
                .withErrorContaining( "Return type of BadReturnTypeSproc#niceSproc must be java.util.stream.Stream" )
                .in( sproc ).onLine( 30 );
    }

    @Test
    public void fails_if_record_type_has_nonpublic_fields()
    {
        JavaFileObject record = JavaFileObjectUtils.resource( "bad_record_type/BadRecord.java" );

        UnsuccessfulCompilationClause compilation = assert_().about( javaSources() )
                .that( asList( forResource( "test_classes/bad_record_type/BadRecordTypeSproc.java" ), record ) )
                .processedWith( processor ).failsToCompile().withErrorCount( 2 );

        compilation.withErrorContaining( "Record definition error: field BadRecord#label must be public" ).in( record )
                .onLine( 22 );

        compilation.withErrorContaining( "Record definition error: field BadRecord#age must be public" ).in( record )
                .onLine( 23 );
    }

    @Test
    public void fails_if_procedure_primitive_input_type_is_not_supported()
    {
        JavaFileObject sproc = JavaFileObjectUtils.resource( "bad_proc_input_type/BadPrimitiveInputSproc.java" );

        assert_().about( javaSource() ).that( sproc ).processedWith( processor ).failsToCompile().withErrorCount( 1 )
                .withErrorContaining(
                        "Unsupported parameter type <short> of procedure BadPrimitiveInputSproc#doSomething" )
                .in( sproc ).onLine( 28 );
    }

    @Test
    public void fails_if_procedure_generic_input_type_is_not_supported()
    {
        JavaFileObject sproc = JavaFileObjectUtils.resource( "bad_proc_input_type/BadGenericInputSproc.java" );

        UnsuccessfulCompilationClause compilation =
                assert_().about( javaSource() ).that( sproc ).processedWith( processor ).failsToCompile()
                        .withErrorCount( 3 );

        compilation.withErrorContaining( "Unsupported parameter type " +
                "<java.util.List<java.util.List<java.util.Map<java.lang.String,java.lang.Thread>>>>" +
                " of procedure BadGenericInputSproc#doSomething" ).in( sproc ).onLine( 32 );

        compilation.withErrorContaining( "Unsupported parameter type " +
                "<java.util.Map<java.lang.String,java.util.List<java.util.concurrent.ExecutorService>>>" +
                " of procedure BadGenericInputSproc#doSomething2" ).in( sproc ).onLine( 38 );

        compilation.withErrorContaining(
                "Unsupported parameter type <java.util.Map> of procedure BadGenericInputSproc#doSomething3" )
                .in( sproc ).onLine( 44 );
    }

    @Test
    public void fails_if_procedure_primitive_record_field_type_is_not_supported()
    {
        JavaFileObject record = JavaFileObjectUtils.resource( "bad_record_field_type/BadRecordSimpleFieldType.java" );

        assert_().about( javaSources() ).that( asList(
                JavaFileObjectUtils.resource( "bad_record_field_type/BadRecordSimpleFieldTypeSproc.java" ), record ) )
                .processedWith( processor ).failsToCompile().withErrorCount( 1 ).withErrorContaining(
                "Record definition error: type of field BadRecordSimpleFieldType#wrongType is not supported" )
                .in( record ).onLine( 25 );
    }

    @Test
    public void fails_if_procedure_generic_record_field_type_is_not_supported()
    {
        JavaFileObject record = JavaFileObjectUtils.resource( "bad_record_field_type/BadRecordGenericFieldType.java" );

        UnsuccessfulCompilationClause compilation = assert_().about( javaSources() ).that( asList(
                JavaFileObjectUtils.resource( "bad_record_field_type/BadRecordGenericFieldTypeSproc.java" ), record ) )
                .processedWith( processor ).failsToCompile().withErrorCount( 3 );

        compilation.withErrorContaining(
                "Record definition error: type of field BadRecordGenericFieldType#wrongType1 is not supported" )
                .in( record ).onLine( 30 );
        compilation.withErrorContaining(
                "Record definition error: type of field BadRecordGenericFieldType#wrongType2 is not supported" )
                .in( record ).onLine( 31 );
        compilation.withErrorContaining(
                "Record definition error: type of field BadRecordGenericFieldType#wrongType3 is not supported" )
                .in( record ).onLine( 32 );
    }

    @Test
    public void fails_if_duplicate_procedures_are_declared()
    {
        JavaFileObject firstDuplicate = JavaFileObjectUtils.resource( "duplicated/Sproc1.java" );
        JavaFileObject secondDuplicate = JavaFileObjectUtils.resource( "duplicated/Sproc2.java" );

        assert_().about( javaSources() ).that( asList( firstDuplicate, secondDuplicate ) ).processedWith( processor )
                .failsToCompile().withErrorCount( 2 ).withErrorContaining(
                "Procedure name <test_classes.duplicated#foobar> is already defined 2 times. " +
                        "It should be defined only once!" );
    }

    @Test
    public void succeeds_to_process_valid_stored_procedures()
    {
        assert_().about( javaSources() )
                .that( asList( JavaFileObjectUtils.resource( "working_procedures/Procedures.java" ),
                        JavaFileObjectUtils.resource( "working_procedures/Records.java" ) ) ).processedWith( processor )
                .compilesWithoutError();

    }

    @Test
    public void fails_if_context_injected_fields_have_wrong_modifiers()
    {
        JavaFileObject sproc = JavaFileObjectUtils.resource( "bad_context_field/BadContextSproc.java" );

        UnsuccessfulCompilationClause unsuccessfulCompilationClause =
                assert_().about( javaSource() ).that( sproc ).processedWith( processor ).failsToCompile()
                        .withErrorCount( 4 );

        unsuccessfulCompilationClause.withErrorContaining(
                "@org.neo4j.procedure.Context usage error: field BadContextSproc#shouldBeNonStatic should be public, non-static and non-final" )
                .in( sproc ).onLine( 26 );

        unsuccessfulCompilationClause.withErrorContaining(
                "@org.neo4j.procedure.Context usage error: field BadContextSproc#shouldBeNonFinal should be public, non-static and non-final" )
                .in( sproc ).onLine( 28 );

        unsuccessfulCompilationClause.withErrorContaining(
                "@org.neo4j.procedure.Context usage error: field BadContextSproc#shouldBePublic should be public, non-static and non-final" )
                .in( sproc ).onLine( 32 );

        unsuccessfulCompilationClause.withErrorContaining(
                "Field BadContextSproc#shouldBeStatic should be static" )
                .in( sproc ).onLine( 33 );
    }
}
