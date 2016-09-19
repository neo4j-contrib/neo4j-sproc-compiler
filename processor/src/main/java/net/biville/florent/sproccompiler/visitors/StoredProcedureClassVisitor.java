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

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.SimpleElementVisitor8;

public class StoredProcedureClassVisitor extends SimpleElementVisitor8<Stream<CompilationError>, Void>
{

    private final Set<TypeElement> visitedElements = new HashSet<>();
    private final FieldVisitor fieldVisitor = new FieldVisitor();

    @Override
    public Stream<CompilationError> visitType( TypeElement e, Void ignored )
    {
        if ( isFirstVisit( e ) )
        {
            return e.getEnclosedElements()
                    .stream()
                    .flatMap( fieldVisitor::visit );
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
}
