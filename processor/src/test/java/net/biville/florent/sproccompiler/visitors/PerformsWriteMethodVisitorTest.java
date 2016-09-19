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
import net.biville.florent.sproccompiler.errors.CompilationError;
import net.biville.florent.sproccompiler.testutils.TypeMirrorTestUtils;
import net.biville.florent.sproccompiler.visitors.examples.PerformsWriteProcedures;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.stream.Stream;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class PerformsWriteMethodVisitorTest
{
    @Rule
    public CompilationRule compilationRule = new CompilationRule();
    private Types types;
    private TypeMirrorTestUtils typeMirrorTestUtils;

    private ElementVisitor<Stream<CompilationError>,Void> visitor = new PerformsWriteMethodVisitor();

    @Before
    public void prepare()
    {
        types = compilationRule.getTypes();
        Elements elements = compilationRule.getElements();
        typeMirrorTestUtils = new TypeMirrorTestUtils( types, elements, new TypeMirrorUtils( types, elements ) );
    }

    @Test
    public void rejects_non_procedure_methods()
    {
        Element element = methodElement( PerformsWriteProcedures.class, "missingProcedureAnnotation" );

        Stream<CompilationError> errors = visitor.visit( element );

        Assertions.assertThat( errors ).hasSize( 1 ).extracting( "element", "errorMessage" ).contains(
                Tuple.tuple( element, "@PerformsWrites usage error: missing @Procedure annotation on method" ) );
    }

    @Test
    public void rejects_conflicted_mode_usage()
    {
        Element element = methodElement( PerformsWriteProcedures.class, "conflictingMode" );

        Stream<CompilationError> errors = visitor.visit( element );

        Assertions.assertThat( errors ).hasSize( 1 ).extracting( "element", "errorMessage" ).contains(
                Tuple.tuple( element, "@PerformsWrites usage error: cannot use mode other than Mode" + ".DEFAULT" ) );
    }

    @Test
    public void validates_regular_procedure()
    {
        Element element = methodElement( PerformsWriteProcedures.class, "ok" );

        Stream<CompilationError> errors = visitor.visit( element );

        Assertions.assertThat( errors ).isEmpty();
    }

    private Element methodElement( Class<?> type, String methodName )
    {
        TypeMirror mirror = typeMirrorTestUtils.typeOf( type );
        return ElementFilter.methodsIn( types.asElement( mirror ).getEnclosedElements() ).stream().filter( method ->
        {
            return method.getSimpleName().contentEquals( methodName );
        } ).findFirst().orElseThrow( () -> new AssertionError(
                String.format( "Could not find method %s of class %s", methodName, type.getName() ) ) );
    }
}
