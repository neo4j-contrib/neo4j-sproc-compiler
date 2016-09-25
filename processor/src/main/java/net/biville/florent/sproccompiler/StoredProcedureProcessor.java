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
package net.biville.florent.sproccompiler;

import com.google.auto.service.AutoService;
import net.biville.florent.sproccompiler.messages.CompilationMessage;
import net.biville.florent.sproccompiler.messages.MessagePrinter;
import net.biville.florent.sproccompiler.validators.DuplicatedStoredProcedureValidator;
import net.biville.florent.sproccompiler.visitors.StoredProcedureVisitor;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
public class StoredProcedureProcessor extends AbstractProcessor
{

    private static final Class<? extends Annotation> sprocType = Procedure.class;
    private static final String IGNORE_CONTEXT_WARNINGS = "IgnoreContextWarnings";
    private final Set<Element> visitedProcedures = new LinkedHashSet<>();

    private Function<Collection<Element>,Stream<CompilationMessage>> duplicateProcedure;
    private ElementVisitor<Stream<CompilationMessage>,Void> storedProcedureVisitor;
    private MessagePrinter messagePrinter;

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
        storedProcedureVisitor = new StoredProcedureVisitor( typeUtils, elementUtils, processingEnv.getOptions().containsKey(
                IGNORE_CONTEXT_WARNINGS ) );
        duplicateProcedure = new DuplicatedStoredProcedureValidator( typeUtils, elementUtils );
    }

    @Override
    public boolean process( Set<? extends TypeElement> annotations, RoundEnvironment roundEnv )
    {

        processStoredProcedures( roundEnv );
        if ( roundEnv.processingOver() )
        {
            duplicateProcedure.apply( visitedProcedures ).forEach( messagePrinter::print );
        }
        return false;
    }

    private void processStoredProcedures( RoundEnvironment roundEnv )
    {
        Set<? extends Element> procedures = roundEnv.getElementsAnnotatedWith( sprocType );
        visitedProcedures.addAll( procedures );
        procedures.stream().flatMap( storedProcedureVisitor::visit ).forEachOrdered( messagePrinter::print );
    }

}
