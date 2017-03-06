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
package net.biville.florent.sproccompiler.export;

import com.google.auto.service.AutoService;
import net.biville.florent.sproccompiler.export.io.DsvSerializer;
import net.biville.florent.sproccompiler.messages.MessagePrinter;
import net.biville.florent.sproccompiler.visitors.ExecutableElementVisitor;
import net.biville.florent.sproccompiler.visitors.TypeElementVisitor;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.neo4j.procedure.Procedure;
import org.neo4j.procedure.UserFunction;

import static java.util.Arrays.asList;

@AutoService( Processor.class )
public class DsvProcessor extends AbstractProcessor
{

    private final Collection<ExecutableElement> visitedProcedures = new LinkedHashSet<>();
    private final Collection<ExecutableElement> visitedFunctions = new LinkedHashSet<>();

    private ExecutableElementVisitor methodVisitor;
    private DsvConfiguration configuration;
    private DsvSerializer serializer;

    @Override
    public Set<String> getSupportedAnnotationTypes()
    {
        return new HashSet<>( asList( Procedure.class.getName(), UserFunction.class.getName() ) );
    }

    @Override
    public Set<String> getSupportedOptions()
    {
        return DsvConfiguration.getSupportedOptions();
    }

    @Override
    public SourceVersion getSupportedSourceVersion()
    {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public synchronized void init( ProcessingEnvironment processingEnv )
    {
        visitedProcedures.clear();
        visitedFunctions.clear();

        Messager messager = processingEnv.getMessager();
        methodVisitor = new ExecutableElementVisitor( messager );
        configuration = new DsvConfiguration( processingEnv.getOptions() );
        serializer = new DsvSerializer( processingEnv.getElementUtils(), new TypeElementVisitor( messager ),
                new MessagePrinter( messager ) );
    }

    @Override
    public boolean process( Set<? extends TypeElement> annotations, RoundEnvironment roundEnv )
    {
        configuration.getRootPath().ifPresent( ( root ) ->
        {
            if ( roundEnv.processingOver() )
            {
                serializer.serialize( root, configuration, visitedProcedures, visitedFunctions );
            }
            else
            {
                roundEnv.getElementsAnnotatedWith( Procedure.class ).forEach( this::visitProcedure );
                roundEnv.getElementsAnnotatedWith( UserFunction.class ).forEach( this::visitFunction );
            }
        } );

        return false;
    }

    private void visitFunction( Element el )
    {
        visitedFunctions.add( methodVisitor.visit( el ) );
    }

    private void visitProcedure( Element el )
    {
        visitedProcedures.add( methodVisitor.visit( el ) );
    }


}
