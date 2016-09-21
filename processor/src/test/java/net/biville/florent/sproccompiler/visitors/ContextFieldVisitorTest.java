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
import net.biville.florent.sproccompiler.messages.CompilationMessage;
import net.biville.florent.sproccompiler.testutils.ElementTestUtils;
import net.biville.florent.sproccompiler.visitors.examples.FinalContextMisuse;
import net.biville.florent.sproccompiler.visitors.examples.NonPublicContextMisuse;
import net.biville.florent.sproccompiler.visitors.examples.StaticContextMisuse;
import net.biville.florent.sproccompiler.visitors.examples.UnsupportedInjectedContextTypes;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.stream.Stream;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class ContextFieldVisitorTest
{

    @Rule
    public CompilationRule compilationRule = new CompilationRule();
    private ElementTestUtils elementTestUtils;
    private ElementVisitor<Stream<CompilationMessage>,Void> contextFieldVisitor;

    @Before
    public void prepare()
    {
        elementTestUtils = new ElementTestUtils( compilationRule );
        contextFieldVisitor = new ContextFieldVisitor( compilationRule.getTypes(), compilationRule.getElements() );
    }

    @Test
    public void rejects_non_public_context_fields()
    {
        Stream<VariableElement> fields = elementTestUtils.getFields( NonPublicContextMisuse.class );

        Stream<CompilationMessage> result = fields.flatMap( contextFieldVisitor::visit );

        assertThat( result ).extracting( CompilationMessage::getCategory, CompilationMessage::getContents )
                .containsExactly( tuple( Diagnostic.Kind.ERROR,
                        "@org.neo4j.procedure.Context usage error: field NonPublicContextMisuse#arithm should be public, " +
                                "non-static and non-final" ) );
    }

    @Test
    public void rejects_static_context_fields()
    {
        Stream<VariableElement> fields = elementTestUtils.getFields( StaticContextMisuse.class );

        Stream<CompilationMessage> result = fields.flatMap( contextFieldVisitor::visit );

        assertThat( result ).extracting( CompilationMessage::getCategory, CompilationMessage::getContents )
                .containsExactly( tuple( Diagnostic.Kind.ERROR,
                        "@org.neo4j.procedure.Context usage error: field StaticContextMisuse#db should be public, non-static " +
                                "and non-final" ) );
    }

    @Test
    public void rejects_final_context_fields()
    {
        Stream<VariableElement> fields = elementTestUtils.getFields( FinalContextMisuse.class );

        Stream<CompilationMessage> result = fields.flatMap( contextFieldVisitor::visit );

        assertThat( result ).extracting( CompilationMessage::getCategory, CompilationMessage::getContents )
                .containsExactly( tuple( Diagnostic.Kind.ERROR,
                        "@org.neo4j.procedure.Context usage error: field FinalContextMisuse#graphDatabaseService should be " +
                                "public, non-static and non-final" ) );
    }

    @Test
    public void warns_against_unsupported_injected_types()
    {
        Stream<VariableElement> fields = elementTestUtils.getFields( UnsupportedInjectedContextTypes.class );

        Stream<CompilationMessage> result = fields.flatMap( contextFieldVisitor::visit );

        assertThat( result ).extracting( CompilationMessage::getCategory, CompilationMessage::getContents )
                .containsExactlyInAnyOrder( tuple( Diagnostic.Kind.WARNING,
                        "@org.neo4j.procedure.Context usage warning: found type: <java.lang.String>, expected one of: <org.neo4j.graphdb.GraphDatabaseService>, <org.neo4j.logging.Log>" ),
                        tuple( Diagnostic.Kind.WARNING,
                                "@org.neo4j.procedure.Context usage warning: found type: <org.neo4j.kernel.internal.GraphDatabaseAPI>, expected one of: <org.neo4j.graphdb.GraphDatabaseService>, <org.neo4j.logging.Log>" ) );
    }

}
