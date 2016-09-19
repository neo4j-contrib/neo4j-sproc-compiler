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

import net.biville.florent.sproccompiler.errors.CompilationError;
import net.biville.florent.sproccompiler.errors.ProcedureMissingPublicNoArgConstructor;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.SimpleElementVisitor8;

import static javax.lang.model.util.ElementFilter.constructorsIn;

public class StoredProcedureClassVisitor extends SimpleElementVisitor8<Stream<CompilationError>,Void>
{

    private final Set<TypeElement> visitedElements = new HashSet<>();
    private final FieldVisitor fieldVisitor = new FieldVisitor();

    @Override
    public Stream<CompilationError> visitType( TypeElement procedureClass, Void ignored )
    {
        if ( isFirstVisit( procedureClass ) )
        {
            return Stream.concat( validateFields( procedureClass ), validateConstructor( procedureClass ) );
        }
        return Stream.empty();
    }

    /**
     * Check if the {@link TypeElement} is visited for the first time. A {@link TypeElement} will be visited once per
     * procedure it contains, but it only needs to be validated once.
     *
     * @param e The visited {@link TypeElement}
     * @return true for the first visit of the {@link TypeElement}, false afterwards
     */
    private boolean isFirstVisit( TypeElement e )
    {
        return visitedElements.add( e );
    }

    private Stream<CompilationError> validateFields( TypeElement e )
    {
        return e.getEnclosedElements().stream().flatMap( fieldVisitor::visit );
    }

    private Stream<CompilationError> validateConstructor( Element procedureClass )
    {
        Optional<ExecutableElement> publicNoArgConstructor =
                constructorsIn( procedureClass.getEnclosedElements() ).stream()
                        .filter( c -> c.getModifiers().contains( Modifier.PUBLIC ) )
                        .filter( c -> c.getParameters().isEmpty() ).findFirst();

        if ( !publicNoArgConstructor.isPresent() )
        {
            return Stream.of( new ProcedureMissingPublicNoArgConstructor( procedureClass,
                    "Procedure class %s should contain a public no-arg constructor, none found.", procedureClass ) );
        }
        return Stream.empty();
    }
}
