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
package net.biville.florent.sproccompiler.testutils;

import com.google.testing.compile.CompilationRule;

import java.util.stream.Stream;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

import static javax.lang.model.util.ElementFilter.fieldsIn;

public class ElementTestUtils
{

    private final Elements elements;

    public ElementTestUtils( CompilationRule compilationRule )
    {
        this( compilationRule.getElements() );
    }

    private ElementTestUtils( Elements elements )
    {
        this.elements = elements;
    }

    public Stream<VariableElement> getFields( Class<?> type )
    {
        TypeElement procedure = elements.getTypeElement( type.getName() );

        return fieldsIn( procedure.getEnclosedElements() ).stream();
    }
}
