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

import net.biville.florent.sproccompiler.messages.CompilationMessage;
import net.biville.florent.sproccompiler.messages.FieldError;

import java.util.Set;
import java.util.stream.Stream;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.lang.model.util.Types;

import org.neo4j.procedure.Context;

public class FieldVisitor extends SimpleElementVisitor8<Stream<CompilationMessage>,Void>
{

    private final ElementVisitor<Stream<CompilationMessage>,Void> contextFieldVisitor;

    public FieldVisitor( Types types, Elements elements, boolean skipContextWarnings )
    {
        contextFieldVisitor = new ContextFieldVisitor( types, elements, skipContextWarnings );
    }

    private static Stream<CompilationMessage> validateNonContextField( VariableElement field )
    {
        Set<Modifier> modifiers = field.getModifiers();
        if ( !modifiers.contains( Modifier.STATIC ) )
        {
            return Stream.of( new FieldError( field, "Field %s#%s should be static",
                    field.getEnclosingElement().getSimpleName(), field.getSimpleName() ) );
        }
        return Stream.empty();
    }

    @Override
    public Stream<CompilationMessage> visitVariable( VariableElement field, Void ignored )
    {
        if ( field.getAnnotation( Context.class ) != null )
        {
            return contextFieldVisitor.visitVariable( field, ignored );
        }
        return validateNonContextField( field );

    }

}
