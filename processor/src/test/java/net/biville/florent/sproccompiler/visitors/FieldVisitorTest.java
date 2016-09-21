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
import net.biville.florent.sproccompiler.visitors.examples.FinalContextMisuse;
import net.biville.florent.sproccompiler.visitors.examples.GoodContextUse;
import net.biville.florent.sproccompiler.visitors.examples.NonPublicContextMisuse;
import net.biville.florent.sproccompiler.visitors.examples.StaticContextMisuse;
import net.biville.florent.sproccompiler.visitors.examples.StaticNonContextMisuse;
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

public class FieldVisitorTest
{

    @Rule
    public CompilationRule compilationRule = new CompilationRule();
    private ElementVisitor<Stream<CompilationMessage>,Void> fieldVisitor = new FieldVisitor();
    private Elements elements;

    @Before
    public void prepare()
    {
        elements = compilationRule.getElements();
    }

    @Test
    public void validates_visibility_of_fields() throws Exception
    {
        Stream<VariableElement> fields = getFields( GoodContextUse.class );

        Stream<CompilationMessage> result = fields.flatMap( fieldVisitor::visit );

        assertThat( result ).isEmpty();
    }

    @Test
    public void rejects_non_public_context_fields() throws Exception
    {
        Stream<VariableElement> fields = getFields( NonPublicContextMisuse.class );

        Stream<CompilationMessage> result = fields.flatMap( fieldVisitor::visit );

        assertThat( result ).extracting( CompilationMessage::getContents ).containsExactly(
                "@org.neo4j.procedure.Context usage error: field NonPublicContextMisuse#arithm should be public, non-static and non-final" );
    }

    @Test
    public void rejects_static_context_fields() throws Exception
    {
        Stream<VariableElement> fields = getFields( StaticContextMisuse.class );

        Stream<CompilationMessage> result = fields.flatMap( fieldVisitor::visit );

        assertThat( result ).extracting( CompilationMessage::getContents ).containsExactly(
                "@org.neo4j.procedure.Context usage error: field StaticContextMisuse#db should be public, non-static and non-final" );
    }

    @Test
    public void rejects_final_context_fields() throws Exception
    {
        Stream<VariableElement> fields = getFields( FinalContextMisuse.class );

        Stream<CompilationMessage> result = fields.flatMap( fieldVisitor::visit );

        assertThat( result ).extracting( CompilationMessage::getContents ).containsExactly(
                "@org.neo4j.procedure.Context usage error: field FinalContextMisuse#kernel should be public, non-static and non-final" );
    }

    @Test
    public void rejects_non_static_non_context_fields() throws Exception
    {
        Stream<VariableElement> fields = getFields( StaticNonContextMisuse.class );

        Stream<CompilationMessage> result = fields.flatMap( fieldVisitor::visit );

        assertThat( result ).extracting( CompilationMessage::getContents ).containsExactly(
                "Field StaticNonContextMisuse#value should be static" );
    }

    private Stream<VariableElement> getFields( Class<?> type )
    {
        TypeElement procedure = elements.getTypeElement( type.getName() );

        return fieldsIn( procedure.getEnclosedElements() ).stream();
    }
}

