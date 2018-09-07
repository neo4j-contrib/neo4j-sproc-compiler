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
package net.biville.florent.sproccompiler.visitors;

import com.google.testing.compile.CompilationRule;
import net.biville.florent.sproccompiler.messages.CompilationMessage;
import net.biville.florent.sproccompiler.testutils.ElementTestUtils;
import net.biville.florent.sproccompiler.visitors.examples.PerformsWriteProcedures;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.stream.Stream;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.tools.Diagnostic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

public class PerformsWriteMethodVisitorTest
{
    @Rule
    public CompilationRule compilationRule = new CompilationRule();

    private ElementVisitor<Stream<CompilationMessage>,Void> visitor = new PerformsWriteMethodVisitor();
    private ElementTestUtils elementTestUtils;

    @Before
    public void prepare()
    {
        elementTestUtils = new ElementTestUtils( compilationRule );
    }

    @Test
    public void rejects_non_procedure_methods()
    {
        Element element =
                elementTestUtils.findMethodElement( PerformsWriteProcedures.class, "missingProcedureAnnotation" );

        Stream<CompilationMessage> errors = visitor.visit( element );

        assertThat( errors ).hasSize( 1 ).extracting( CompilationMessage::getCategory, CompilationMessage::getElement,
                CompilationMessage::getContents ).contains( tuple( Diagnostic.Kind.ERROR, element,
                "@PerformsWrites usage error: missing @Procedure annotation on method" ) );
    }

    @Test
    public void rejects_conflicted_mode_usage()
    {
        Element element = elementTestUtils.findMethodElement( PerformsWriteProcedures.class, "conflictingMode" );

        Stream<CompilationMessage> errors = visitor.visit( element );

        assertThat( errors ).hasSize( 1 ).extracting( CompilationMessage::getCategory, CompilationMessage::getElement,
                CompilationMessage::getContents ).contains( tuple( Diagnostic.Kind.ERROR, element,
                "@PerformsWrites usage error: cannot use mode other than Mode.DEFAULT" ) );
    }

    @Test
    public void validates_regular_procedure()
    {
        Element element = elementTestUtils.findMethodElement( PerformsWriteProcedures.class, "ok" );

        Stream<CompilationMessage> errors = visitor.visit( element );

        assertThat( errors ).isEmpty();
    }
}
