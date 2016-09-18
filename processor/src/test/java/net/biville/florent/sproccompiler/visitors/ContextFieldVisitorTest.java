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
package net.biville.florent.sproccompiler.visitors;

import com.google.testing.compile.CompilationRule;
import net.biville.florent.sproccompiler.errors.CompilationError;
import net.biville.florent.sproccompiler.visitors.examples.FinalContextMisuse;
import net.biville.florent.sproccompiler.visitors.examples.GoodContextUse;
import net.biville.florent.sproccompiler.visitors.examples.NonPublicContextMisuse;
import net.biville.florent.sproccompiler.visitors.examples.StaticContextMisuse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import java.util.stream.Stream;

import static javax.lang.model.util.ElementFilter.fieldsIn;
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
        TypeElement procedure = elements.getTypeElement(type.getName());

        return fieldsIn(procedure.getEnclosedElements()).stream();
    }
}

