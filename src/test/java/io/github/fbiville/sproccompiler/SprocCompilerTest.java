package io.github.fbiville.sproccompiler;

import com.google.testing.compile.CompilationRule;
import com.google.testing.compile.CompileTester;
import org.junit.Rule;
import org.junit.Test;

import javax.annotation.processing.Processor;
import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaFileObjects.forResource;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Arrays.asList;

public class SprocCompilerTest {

    @Rule public CompilationRule compilation = new CompilationRule();
    Processor sprocCompiler = new SprocCompiler();

    @Test
    public void fails_if_parameters_are_not_properly_annotated() {
        JavaFileObject sproc = forResource("test_classes/missing_name/MissingNameSproc.java");

        CompileTester.UnsuccessfulCompilationClause compilation = assert_().about(javaSource())
                .that(sproc)
                .processedWith(sprocCompiler)
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
        JavaFileObject sproc = forResource("test_classes/bad_return_type/BadReturnTypeSproc.java");

        assert_().about(javaSource())
                .that(sproc)
                .processedWith(sprocCompiler)
                .failsToCompile()
                .withErrorCount(1)
                .withErrorContaining("Return type of BadReturnTypeSproc#niceSproc must be java.util.stream.Stream")
                .in(sproc).onLine(13);
    }

    @Test
    public void fails_if_record_type_has_nonpublic_fields() {
        JavaFileObject record = forResource("test_classes/bad_record_type/BadRecord.java");

        CompileTester.UnsuccessfulCompilationClause compilation = assert_().about(javaSources())
                .that(asList(forResource("test_classes/bad_record_type/BadRecordTypeSproc.java"), record))
                .processedWith(sprocCompiler)
                .failsToCompile()
                .withErrorCount(2);

        compilation.withErrorContaining("Field BadRecord#label must be public")
                .in(record).onLine(6);

        compilation.withErrorContaining("Field BadRecord#age must be public")
                .in(record).onLine(7);
    }
}