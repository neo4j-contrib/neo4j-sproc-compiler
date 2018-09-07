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
package net.biville.florent.sproccompiler.validators;

import net.biville.florent.sproccompiler.messages.CompilationMessage;
import net.biville.florent.sproccompiler.messages.DuplicatedProcedureError;
import net.biville.florent.sproccompiler.visitors.AnnotationTypeVisitor;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;

import org.neo4j.procedure.Procedure;

import static java.util.stream.Collectors.groupingBy;

public class DuplicatedProcedureValidator<T extends Annotation>
        implements Function<Collection<Element>,Stream<CompilationMessage>>
{

    private final Elements elements;
    private final Class<T> annotationType;
    private final Function<T,Optional<String>> customNameExtractor;

    public DuplicatedProcedureValidator( Elements elements, Class<T> annotationType,
            Function<T,Optional<String>> customNameExtractor )
    {
        this.elements = elements;
        this.annotationType = annotationType;
        this.customNameExtractor = customNameExtractor;
    }

    @Override
    public Stream<CompilationMessage> apply( Collection<Element> visitedProcedures )
    {
        return findDuplicates( visitedProcedures );
    }

    private Stream<CompilationMessage> findDuplicates( Collection<Element> visitedProcedures )
    {
        return indexByName( visitedProcedures ).filter( index -> index.getValue().size() > 1 )
                .flatMap( this::asErrors );
    }

    private Stream<Map.Entry<String,List<Element>>> indexByName( Collection<Element> visitedProcedures )
    {
        return visitedProcedures.stream().collect( groupingBy( this::getName ) ).entrySet().stream();
    }

    private String getName( Element procedure )
    {
        T annotation = procedure.getAnnotation( annotationType );
        Optional<String> customName = customNameExtractor.apply( annotation );
        return customName.orElse( defaultQualifiedName( procedure ) );
    }

    private String defaultQualifiedName( Element procedure )
    {
        return String.format( "%s.%s", elements.getPackageOf( procedure ).toString(), procedure.getSimpleName() );
    }

    private Stream<CompilationMessage> asErrors( Map.Entry<String,List<Element>> indexedProcedures )
    {
        String duplicatedName = indexedProcedures.getKey();
        return indexedProcedures.getValue().stream()
                .map( procedure -> asError( procedure, duplicatedName, indexedProcedures.getValue().size() ) );
    }

    private CompilationMessage asError( Element procedure, String duplicatedName, int duplicateCount )
    {
        return new DuplicatedProcedureError( procedure, getAnnotationMirror( procedure ),
                "Procedure|function name <%s> is already defined %s times. It should be defined only once!",
                duplicatedName, String.valueOf( duplicateCount ) );
    }

    private AnnotationMirror getAnnotationMirror( Element procedure )
    {
        return procedure.getAnnotationMirrors().stream().filter( this::isProcedureAnnotationType ).findFirst()
                .orElse( null );
    }

    private boolean isProcedureAnnotationType( AnnotationMirror mirror )
    {
        return new AnnotationTypeVisitor( Procedure.class ).visit( mirror.getAnnotationType().asElement() );
    }

}
