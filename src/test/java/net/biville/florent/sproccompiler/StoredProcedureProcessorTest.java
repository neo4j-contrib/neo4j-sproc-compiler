package net.biville.florent.sproccompiler;

import com.google.testing.compile.CompilationRule;
import com.google.testing.compile.CompileTester.UnsuccessfulCompilationClause;
import org.junit.Rule;
import org.junit.Test;

import javax.annotation.processing.Processor;
import javax.tools.JavaFileObject;
import java.net.URL;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaFileObjects.forResource;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Arrays.asList;

public class StoredProcedureProcessorTest {

    @Rule public CompilationRule compilation = new CompilationRule();

    private Processor processor = new StoredProcedureProcessor();

    @Test
    public void fails_if_parameters_are_not_properly_annotated() {
        JavaFileObject sproc = forResource(at("missing_name/MissingNameSproc.java"));

        UnsuccessfulCompilationClause compilation = assert_().about(javaSource())
                .that(sproc)
                .processedWith(processor)
                .failsToCompile()
                .withErrorCount(2);

        compilation
                .withErrorContaining("Missing @org.neo4j.procedure.Name on parameter <parameter>")
                .in(sproc).onLine(18);

        compilation
                .withErrorContaining("Missing @org.neo4j.procedure.Name on parameter <otherParam>")
                .in(sproc).onLine(18);
    }

    @Test
    public void fails_if_return_type_is_not_stream() {
        JavaFileObject sproc = forResource(at("bad_return_type/BadReturnTypeSproc.java"));

        assert_().about(javaSource())
                .that(sproc)
                .processedWith(processor)
                .failsToCompile()
                .withErrorCount(1)
                .withErrorContaining("Return type of BadReturnTypeSproc#niceSproc must be java.util.stream.Stream")
                .in(sproc).onLine(13);
    }

    @Test
    public void fails_if_record_type_has_nonpublic_fields() {
        JavaFileObject record = forResource(at("bad_record_type/BadRecord.java"));

        UnsuccessfulCompilationClause compilation = assert_().about(javaSources())
                .that(asList(forResource("test_classes/bad_record_type/BadRecordTypeSproc.java"), record))
                .processedWith(processor)
                .failsToCompile()
                .withErrorCount(2);

        compilation.withErrorContaining("Field BadRecord#label must be public")
                .in(record).onLine(6);

        compilation.withErrorContaining("Field BadRecord#age must be public")
                .in(record).onLine(7);
    }

    @Test
    public void fails_if_procedure_primitive_input_type_is_not_supported() {
        JavaFileObject sproc = forResource(at("bad_proc_input_type/BadPrimitiveInputSproc.java"));

        assert_().about(javaSource())
                .that(sproc)
                .processedWith(processor)
                .failsToCompile()
                .withErrorCount(1)
                .withErrorContaining(
                    "Unsupported parameter type <short> of procedure BadPrimitiveInputSproc#doSomething"
                ).in(sproc).onLine(9);
    }

    @Test
    public void fails_if_procedure_generic_input_type_is_not_supported() {
        JavaFileObject sproc = forResource(at("bad_proc_input_type/BadGenericInputSproc.java"));

        UnsuccessfulCompilationClause compilation = assert_().about(javaSource())
                .that(sproc)
                .processedWith(processor)
                .failsToCompile()
                .withErrorCount(2);

        compilation
                .withErrorContaining(
                    "Unsupported parameter type " +
                    "<java.util.List<java.util.List<java.util.Map<java.lang.String,java.lang.Thread>>>>" +
                    " of procedure BadGenericInputSproc#doSomething"
                ).in(sproc).onLine(12);

        compilation
                .withErrorContaining(
                    "Unsupported parameter type " +
                    "<java.util.Map<java.lang.String,java.util.List<java.lang.Object>>>" +
                    " of procedure BadGenericInputSproc#doSomething2"
                ).in(sproc).onLine(17);
    }

    @Test
    public void fails_if_procedure_primitive_record_field_type_is_not_supported() {
        JavaFileObject record = forResource(at("bad_record_field_type/BadRecordSimpleFieldType.java"));

        assert_().about(javaSources())
                .that(asList(forResource(at("bad_record_field_type/BadRecordSimpleFieldTypeSproc.java")), record))
                .processedWith(processor)
                .failsToCompile()
                .withErrorCount(1)
                .withErrorContaining(
                    "Type of field BadRecordSimpleFieldType#wrongType is not supported"
                ).in(record).onLine(9);
    }

    @Test
    public void fails_if_procedure_generic_record_field_type_is_not_supported() {
        JavaFileObject record = forResource(at("bad_record_field_type/BadRecordGenericFieldType.java"));

        UnsuccessfulCompilationClause compilation = assert_().about(javaSources())
                .that(asList(forResource(at("bad_record_field_type/BadRecordGenericFieldTypeSproc.java")), record))
                .processedWith(processor)
                .failsToCompile()
                .withErrorCount(3);

        compilation
                .withErrorContaining(
                    "Type of field BadRecordGenericFieldType#wrongType1 is not supported"
                ).in(record).onLine(14);
        compilation
                .withErrorContaining(
                    "Type of field BadRecordGenericFieldType#wrongType2 is not supported"
                ).in(record).onLine(15);
        compilation
                .withErrorContaining(
                    "Type of field BadRecordGenericFieldType#wrongType3 is not supported"
                ).in(record).onLine(16);
    }

    @Test
    public void fails_if_context_injected_fields_have_wrong_modifiers() {
        JavaFileObject sproc = forResource(at("bad_context_field/BadContextSproc.java"));

        UnsuccessfulCompilationClause unsuccessfulCompilationClause = assert_().about(javaSource())
                .that(sproc)
                .processedWith(processor)
                .failsToCompile()
                .withErrorCount(3);

        unsuccessfulCompilationClause
                .withErrorContaining("Field BadContextSproc#shouldBePublic should be public, non-static and non-final")
                .in(sproc).onLine(9);
        unsuccessfulCompilationClause
                .withErrorContaining("Field BadContextSproc#shouldBeNonStatic should be public, non-static and non-final")
                .in(sproc).onLine(10);
        unsuccessfulCompilationClause
                .withErrorContaining("Field BadContextSproc#shouldBeNonFinal should be public, non-static and non-final")
                .in(sproc).onLine(11);
    }

    @Test
    public void fails_if_duplicate_procedures_are_declared() {
        JavaFileObject firstDuplicate = forResource(at("duplicated/Sproc1.java"));
        JavaFileObject secondDuplicate = forResource(at("duplicated/Sproc2.java"));

        assert_().about(javaSources())
                .that(asList(firstDuplicate, secondDuplicate))
                .processedWith(processor)
                .failsToCompile()
                .withErrorCount(1)
                .withErrorContaining("Package <duplicated> contains 2 definitions of procedure <foobar>. Offending classes: <Sproc1,Sproc2>");
    }

    private URL at(String resource) {
        return this.getClass().getResource(String.format("/test_classes/%s", resource));
    }
}