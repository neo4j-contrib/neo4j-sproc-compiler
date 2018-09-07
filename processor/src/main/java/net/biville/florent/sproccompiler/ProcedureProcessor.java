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
package net.biville.florent.sproccompiler;

import com.google.auto.service.AutoService;
import net.biville.florent.sproccompiler.messages.CompilationMessage;
import net.biville.florent.sproccompiler.messages.MessagePrinter;
import net.biville.florent.sproccompiler.validators.DuplicatedProcedureValidator;
import net.biville.florent.sproccompiler.visitors.StoredProcedureVisitor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.neo4j.procedure.Procedure;

@AutoService( Processor.class )
public class ProcedureProcessor extends AbstractProcessor
{

    private static final Class<Procedure> sprocType = Procedure.class;
    private static final String IGNORE_CONTEXT_WARNINGS = "IgnoreContextWarnings";
    private final Set<Element> visitedProcedures = new LinkedHashSet<>();

    private Function<Collection<Element>,Stream<CompilationMessage>> duplicationPredicate;
    private ElementVisitor<Stream<CompilationMessage>,Void> visitor;
    private MessagePrinter messagePrinter;

    public static Optional<String> getCustomName( Procedure proc )
    {
        String name = proc.name();
        if ( !name.isEmpty() )
        {
            return Optional.of( name );
        }
        String value = proc.value();
        if ( !value.isEmpty() )
        {
            return Optional.of( value );
        }
        return Optional.empty();
    }

    @Override
    public Set<String> getSupportedOptions()
    {
        return Collections.singleton( IGNORE_CONTEXT_WARNINGS );
    }

    @Override
    public Set<String> getSupportedAnnotationTypes()
    {
        Set<String> types = new HashSet<>();
        types.add( sprocType.getName() );
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion()
    {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public synchronized void init( ProcessingEnvironment processingEnv )
    {
        super.init( processingEnv );
        Types typeUtils = processingEnv.getTypeUtils();
        Elements elementUtils = processingEnv.getElementUtils();

        visitedProcedures.clear();
        messagePrinter = new MessagePrinter( processingEnv.getMessager() );
        visitor = new StoredProcedureVisitor( typeUtils, elementUtils,
                processingEnv.getOptions().containsKey( IGNORE_CONTEXT_WARNINGS ) );
        duplicationPredicate =
                new DuplicatedProcedureValidator<>( elementUtils, sprocType, ProcedureProcessor::getCustomName );
    }

    @Override
    public boolean process( Set<? extends TypeElement> annotations, RoundEnvironment roundEnv )
    {

        processElements( roundEnv );
        if ( roundEnv.processingOver() )
        {
            duplicationPredicate.apply( visitedProcedures ).forEach( messagePrinter::print );
        }
        return false;
    }

    private void processElements( RoundEnvironment roundEnv )
    {
        Set<? extends Element> procedures = roundEnv.getElementsAnnotatedWith( sprocType );
        visitedProcedures.addAll( procedures );
        procedures.stream().flatMap( this::validate ).forEachOrdered( messagePrinter::print );
    }

    private Stream<CompilationMessage> validate( Element element )
    {
        return visitor.visit( element );
    }

}
