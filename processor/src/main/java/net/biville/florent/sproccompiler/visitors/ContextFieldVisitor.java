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

import net.biville.florent.sproccompiler.messages.CompilationMessage;
import net.biville.florent.sproccompiler.messages.ContextFieldWarning;
import net.biville.florent.sproccompiler.messages.FieldError;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.lang.model.util.Types;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.logging.Log;
import org.neo4j.procedure.Context;

class ContextFieldVisitor extends SimpleElementVisitor8<Stream<CompilationMessage>,Void>
{
    private static final Set<Class<?>> SUPPORTED_TYPES =
            new LinkedHashSet<>( Arrays.asList( GraphDatabaseService.class, Log.class ) );

    private final Elements elements;
    private final Types types;
    private final boolean skipContextWarnings;

    public ContextFieldVisitor( Types types, Elements elements, boolean skipContextWarnings )
    {
        this.elements = elements;
        this.types = types;
        this.skipContextWarnings = skipContextWarnings;
    }

    private static String types( Set<Class<?>> supportedTypes )
    {
        return supportedTypes.stream().map( Class::getName ).collect( Collectors.joining( ">, <", "<", ">" ) );
    }

    @Override
    public Stream<CompilationMessage> visitVariable( VariableElement field, Void ignored )
    {
        return Stream.concat( validateModifiers( field ), validateInjectedTypes( field ) );
    }

    private Stream<CompilationMessage> validateModifiers( VariableElement field )
    {
        if ( !hasValidModifiers( field ) )
        {
            return Stream.of( new FieldError( field,
                    "@%s usage error: field %s#%s should be public, non-static and non-final", Context.class.getName(),
                    field.getEnclosingElement().getSimpleName(), field.getSimpleName() ) );
        }

        return Stream.empty();
    }

    private Stream<CompilationMessage> validateInjectedTypes( VariableElement field )
    {
        if ( skipContextWarnings )
        {
            return Stream.empty();
        }

        TypeMirror fieldType = field.asType();
        if ( !injectsAllowedTypes( fieldType ) )
        {
            return Stream
                    .of( new ContextFieldWarning( field, "@%s usage warning: found type: <%s>, expected one of: %s",
                            Context.class.getName(), fieldType.toString(), types( SUPPORTED_TYPES ) ) );
        }

        return Stream.empty();
    }

    private boolean injectsAllowedTypes( TypeMirror fieldType )
    {
        return supportedTypeMirrors( SUPPORTED_TYPES ).filter( t -> types.isSameType( t, fieldType ) ).findAny()
                .isPresent();
    }

    private boolean hasValidModifiers( VariableElement field )
    {
        Set<Modifier> modifiers = field.getModifiers();
        return modifiers.contains( Modifier.PUBLIC ) && !modifiers.contains( Modifier.STATIC ) &&
                !modifiers.contains( Modifier.FINAL );
    }

    private Stream<TypeMirror> supportedTypeMirrors( Set<Class<?>> supportedTypes )
    {
        return supportedTypes.stream().map( c -> elements.getTypeElement( c.getName() ).asType() );
    }
}
