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
import net.biville.florent.sproccompiler.compilerutils.TypeMirrorUtils;
import net.biville.florent.sproccompiler.messages.CompilationMessage;
import net.biville.florent.sproccompiler.testutils.ElementTestUtils;
import net.biville.florent.sproccompiler.visitors.examples.UserFunctionsExamples;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.stream.Stream;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

public class UserFunctionVisitorTest
{
    @Rule
    public CompilationRule compilationRule = new CompilationRule();
    private ElementTestUtils elementTestUtils;
    private ElementVisitor<Stream<CompilationMessage>,Void> visitor;

    @Before
    public void prepare()
    {
        Types types = compilationRule.getTypes();
        Elements elements = compilationRule.getElements();

        elementTestUtils = new ElementTestUtils( compilationRule );
        visitor = new UserFunctionVisitor( types, elements, new TypeMirrorUtils( types, elements ) );
    }

    @Test
    public void functions_with_specified_name_cannot_be_in_root_namespace()
    {
        Element function = elementTestUtils.findMethodElement( UserFunctionsExamples.class, "functionWithName" );

        Stream<CompilationMessage> errors = visitor.visit( function );

        assertThat( errors ).hasSize( 1 ).extracting( CompilationMessage::getCategory, CompilationMessage::getElement,
                CompilationMessage::getContents ).contains( tuple( Diagnostic.Kind.ERROR, function,
                "Function <in_root_namespace> cannot be defined in the root namespace. Valid name example: com.acme.my_function" ) );
    }

    @Test
    public void functions_with_specified_value_cannot_be_in_root_namespace()
    {
        Element function = elementTestUtils.findMethodElement( UserFunctionsExamples.class, "functionWithValue" );

        Stream<CompilationMessage> errors = visitor.visit( function );

        assertThat( errors ).hasSize( 1 ).extracting( CompilationMessage::getCategory, CompilationMessage::getElement,
                CompilationMessage::getContents ).contains( tuple( Diagnostic.Kind.ERROR, function,
                "Function <in_root_namespace_again> cannot be defined in the root namespace. Valid name example: com.acme.my_function" ) );
    }

    @Test
    public void functions_in_non_root_namespace_are_valid()
    {
        Element function = elementTestUtils.findMethodElement( UserFunctionsExamples.class, "ok" );

        Stream<CompilationMessage> errors = visitor.visit( function );

        assertThat( errors ).isEmpty();
    }

    @Test
    public void functions_with_unsupported_return_types_are_invalid()
    {
        Element function = elementTestUtils.findMethodElement( UserFunctionsExamples.class, "wrongReturnType" );

        Stream<CompilationMessage> errors = visitor.visit( function );

        assertThat( errors ).hasSize( 1 ).extracting( CompilationMessage::getCategory, CompilationMessage::getElement,
                CompilationMessage::getContents ).contains( tuple( Diagnostic.Kind.ERROR, function,
                "Unsupported return type <void> of function defined in <net.biville.florent.sproccompiler.visitors.examples.UserFunctionsExamples#wrongReturnType>." ) );
    }

    @Test
    public void functions_with_unsupported_parameter_types_are_invalid()
    {
        Element function = elementTestUtils.findMethodElement( UserFunctionsExamples.class, "wrongParameterType" );

        Stream<CompilationMessage> errors = visitor.visit( function );

        assertThat( errors ).hasSize( 1 ).extracting( CompilationMessage::getCategory, CompilationMessage::getContents )
                .contains( tuple( Diagnostic.Kind.ERROR,
                        "Unsupported parameter type <java.lang.Thread> of procedure|function " +
                                "UserFunctionsExamples#wrongParameterType" ) );
    }

    @Test
    public void functions_with_non_annotated_parameters_are_invalid()
    {
        Element function =
                elementTestUtils.findMethodElement( UserFunctionsExamples.class, "missingParameterAnnotation" );

        Stream<CompilationMessage> errors = visitor.visit( function );

        assertThat( errors ).hasSize( 1 ).extracting( CompilationMessage::getCategory, CompilationMessage::getContents )
                .contains( tuple( Diagnostic.Kind.ERROR,
                        "@org.neo4j.procedure.Name usage error: missing on parameter <arg1>" ) );
    }
}
