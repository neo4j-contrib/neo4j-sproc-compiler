package net.biville.florent.sproccompiler.visitors;

import com.google.testing.compile.CompilationRule;
import net.biville.florent.sproccompiler.errors.CompilationError;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.api.KernelTransaction;
import org.neo4j.logging.Log;
import org.neo4j.procedure.Context;

import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ContextFieldVisitorTest {

    @Rule public CompilationRule compilationRule = new CompilationRule();
    private ElementVisitor<Stream<CompilationError>, Void> contextFieldVisitor = new ContextFieldVisitor();
    private Elements elements;

    @Before
    public void prepare() {
        elements = compilationRule.getElements();
    }

    @Test
    public void validates_visibility_of_fields() throws Exception {
        Stream<VariableElement> fields = getFields(GoodContextUse.class);

        Stream<CompilationError> result = fields.flatMap(contextFieldVisitor::visit);

        assertThat(result).isEmpty();
    }

    @Test
    public void rejects_non_public_fields() throws Exception {
        Stream<VariableElement> fields = getFields(NonPublicContextMisuse.class);

        Stream<CompilationError> result = fields.flatMap(contextFieldVisitor::visit);

        assertThat(result)
                .extracting(CompilationError::getErrorMessage)
                .containsExactly("@org.neo4j.procedure.Context usage error: field NonPublicContextMisuse#arithm should be public, non-static and non-final");
    }

    @Test
    public void rejects_static_fields() throws Exception {
        Stream<VariableElement> fields = getFields(StaticContextMisuse.class);

        Stream<CompilationError> result = fields.flatMap(contextFieldVisitor::visit);

        assertThat(result)
                .extracting(CompilationError::getErrorMessage)
                .containsExactly("@org.neo4j.procedure.Context usage error: field StaticContextMisuse#db should be public, non-static and non-final");
    }

    @Test
    public void rejects_final_fields() throws Exception {
        Stream<VariableElement> fields = getFields(FinalContextMisuse.class);

        Stream<CompilationError> result = fields.flatMap(contextFieldVisitor::visit);

        assertThat(result)
                .extracting(CompilationError::getErrorMessage)
                .containsExactly("@org.neo4j.procedure.Context usage error: field FinalContextMisuse#kernel should be public, non-static and non-final");
    }

    private Stream<VariableElement> getFields(Class<?> type) {
        TypeElement procedure = elements.getTypeElement(type.getCanonicalName());
        return ElementFilter.fieldsIn(procedure.getEnclosedElements()).stream();
    }
}

class GoodContextUse {
    @Context public GraphDatabaseService db;
}

class NonPublicContextMisuse {
    @Context Log arithm;
}

class StaticContextMisuse {
    @Context public static GraphDatabaseService db;
}

class FinalContextMisuse {
    @Context public final KernelTransaction kernel = null;
}