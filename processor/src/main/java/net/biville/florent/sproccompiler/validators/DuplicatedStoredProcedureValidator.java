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
package net.biville.florent.sproccompiler.validators;

import net.biville.florent.sproccompiler.messages.CompilationMessage;
import net.biville.florent.sproccompiler.messages.DuplicatedProcedureError;
import net.biville.florent.sproccompiler.visitors.AnnotationTypeVisitor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.neo4j.procedure.Procedure;

import static java.util.stream.Collectors.groupingBy;

public class DuplicatedStoredProcedureValidator implements Function<Collection<Element>,Stream<CompilationMessage>>
{

    private final Types types;
    private final Elements elements;

    public DuplicatedStoredProcedureValidator( Types types, Elements elements )
    {
        this.types = types;
        this.elements = elements;
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
        return visitedProcedures.stream().collect( groupingBy( this::getProcedureName ) ).entrySet().stream();
    }

    private String getProcedureName( Element procedure )
    {
        Procedure annotation = procedure.getAnnotation( Procedure.class );
        String override = annotation.value();
        if ( !override.isEmpty() )
        {
            return override;
        }
        return defaultQualifiedName( procedure );
    }

    private String defaultQualifiedName( Element procedure )
    {
        return String.format( "%s#%s", elements.getPackageOf( procedure ).toString(), procedure.getSimpleName() );
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
                "Procedure name <%s> is already defined %s times. It should be defined only once!", duplicatedName,
                String.valueOf( duplicateCount ) );
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
