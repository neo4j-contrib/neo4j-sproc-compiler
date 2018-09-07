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
package net.biville.florent.sproccompiler.testutils;

import com.google.testing.compile.CompilationRule;

import java.util.stream.Stream;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static javax.lang.model.util.ElementFilter.fieldsIn;

public class ElementTestUtils
{

    private final Elements elements;
    private final Types types;
    private final TypeMirrorTestUtils typeMirrorTestUtils;

    public ElementTestUtils( CompilationRule rule )
    {
        this( rule.getElements(), rule.getTypes(), new TypeMirrorTestUtils( rule ) );
    }

    private ElementTestUtils( Elements elements, Types types, TypeMirrorTestUtils typeMirrorTestUtils )
    {
        this.elements = elements;
        this.types = types;
        this.typeMirrorTestUtils = typeMirrorTestUtils;
    }

    public Stream<VariableElement> getFields( Class<?> type )
    {
        TypeElement procedure = elements.getTypeElement( type.getName() );

        return fieldsIn( procedure.getEnclosedElements() ).stream();
    }

    public Element findMethodElement( Class<?> type, String methodName )
    {
        TypeMirror mirror = typeMirrorTestUtils.typeOf( type );
        return ElementFilter.methodsIn( types.asElement( mirror ).getEnclosedElements() ).stream()
                .filter( method -> method.getSimpleName().contentEquals( methodName ) ).findFirst().orElseThrow(
                        () -> new AssertionError(
                                String.format( "Could not find method %s of class %s", methodName, type.getName() ) ) );
    }
}
