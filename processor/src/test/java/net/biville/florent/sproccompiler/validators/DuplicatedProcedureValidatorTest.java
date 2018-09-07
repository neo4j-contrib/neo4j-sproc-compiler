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

import com.google.testing.compile.CompilationRule;
import net.biville.florent.sproccompiler.ProcedureProcessor;
import net.biville.florent.sproccompiler.messages.CompilationMessage;
import net.biville.florent.sproccompiler.validators.examples.DefaultProcedureA;
import net.biville.florent.sproccompiler.validators.examples.DefaultProcedureB;
import net.biville.florent.sproccompiler.validators.examples.OverriddenProcedureB;
import net.biville.florent.sproccompiler.validators.examples.override.OverriddenProcedureA;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import org.neo4j.procedure.Procedure;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

public class DuplicatedProcedureValidatorTest
{

    @Rule
    public CompilationRule compilation = new CompilationRule();

    private Elements elements;
    private Function<Collection<Element>,Stream<CompilationMessage>> validator;

    @Before
    public void prepare()
    {
        elements = compilation.getElements();
        validator = new DuplicatedProcedureValidator<>( elements, Procedure.class, ProcedureProcessor::getCustomName );
    }

    @Test
    public void detects_duplicate_procedure_with_default_names()
    {
        Element procedureA = procedureMethod( DefaultProcedureA.class.getName() );
        Element procedureB = procedureMethod( DefaultProcedureB.class.getName() );
        Collection<Element> duplicates = asList( procedureA, procedureB );

        Stream<CompilationMessage> errors = validator.apply( duplicates );

        String procedureName = "net.biville.florent.sproccompiler.validators.examples.procedure";
        assertThat( errors ).extracting( CompilationMessage::getCategory, CompilationMessage::getElement,
                CompilationMessage::getContents ).containsExactlyInAnyOrder( tuple( Diagnostic.Kind.ERROR, procedureA,
                "Procedure|function name <" + procedureName + "> is already defined 2 times. It should be defined " +
                        "only once!" ), tuple( Diagnostic.Kind.ERROR, procedureB,
                "Procedure|function name <" + procedureName +
                        "> is already defined 2 times. It should be defined only once!" ) );
    }

    @Test
    public void detects_duplicate_procedure_with_overridden_names()
    {
        Element procedureA = procedureMethod( OverriddenProcedureA.class.getName() );
        Element procedureB = procedureMethod( OverriddenProcedureB.class.getName() );
        Collection<Element> duplicates = asList( procedureA, procedureB );

        Stream<CompilationMessage> errors = validator.apply( duplicates );

        assertThat( errors ).extracting( CompilationMessage::getCategory, CompilationMessage::getElement,
                CompilationMessage::getContents ).containsExactlyInAnyOrder( tuple( Diagnostic.Kind.ERROR, procedureA,
                "Procedure|function name <override> is already defined 2 times. It should be defined only once!" ),
                tuple( Diagnostic.Kind.ERROR, procedureB,
                        "Procedure|function name <override> is already defined 2 times. It should be defined only " +
                                "once!" ) );
    }

    @Test
    public void does_not_detect_duplicates_if_duplicate_procedure_has_custom_name()
    {
        Collection<Element> duplicates = asList( procedureMethod( DefaultProcedureA.class.getName() ),
                procedureMethod( OverriddenProcedureB.class.getName() ) );

        Stream<CompilationMessage> errors = validator.apply( duplicates );

        assertThat( errors ).isEmpty();
    }

    private Element procedureMethod( String name )
    {
        TypeElement typeElement = elements.getTypeElement( name );
        Collection<Element> procedures = findProcedures( typeElement );
        if ( procedures.size() != 1 )
        {
            throw new AssertionError( "Test procedure class should only have 1 defined procedure" );
        }
        return procedures.iterator().next();
    }

    private Collection<Element> findProcedures( TypeElement typeElement )
    {
        return typeElement.getEnclosedElements().stream()
                .filter( element -> element.getAnnotation( Procedure.class ) != null )
                .collect( Collectors.<Element>toList() );
    }

}
