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
import net.biville.florent.sproccompiler.export.messages.DsvExportError;
import net.biville.florent.sproccompiler.messages.MessagePrinter;
import net.biville.florent.sproccompiler.visitors.ExecutableElementVisitor;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import org.neo4j.procedure.Procedure;
import org.neo4j.procedure.UserFunction;

import static java.util.Arrays.asList;

@AutoService( Processor.class )
public class DsvProcessor extends AbstractProcessor
{

    private final Map<PackageElement,Collection<ExecutableElement>> visitedMethods = new HashMap<>();
    private Elements elementUtils;
    private ElementVisitor<ExecutableElement,Void> methodVisitor;
    private DsvConfiguration dsvConfiguration;
    private DsvFieldExporter fieldExporter;
    private MessagePrinter messagePrinter;

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
        visitedMethods.clear();
        elementUtils = processingEnv.getElementUtils();
        methodVisitor = new ExecutableElementVisitor( processingEnv.getMessager() );
        messagePrinter = new MessagePrinter( processingEnv.getMessager() );
        fieldExporter = new DsvFieldExporter( elementUtils );
        dsvConfiguration = new DsvConfiguration( processingEnv.getOptions() );
    }

    @Override
    public boolean process( Set<? extends TypeElement> annotations, RoundEnvironment roundEnv )
    {
        dsvConfiguration.getRootPath().ifPresent( ( root ) ->
        {
            if ( !roundEnv.processingOver() )
            {
                roundEnv.getElementsAnnotatedWith( Procedure.class ).forEach( this::index );
                roundEnv.getElementsAnnotatedWith( UserFunction.class ).forEach( this::index );
            }
            else
            {
                generateDocumentation( root );
            }
        } );

        return false;
    }

    private void index( Element el )
    {
        ExecutableElement method = methodVisitor.visit( el );
        visitedMethods.computeIfAbsent( elementUtils.getPackageOf( method ), ( k ) -> new ArrayList<>() ).add( method );
    }

    private void generateDocumentation( Path root )
    {
        visitedMethods.entrySet().forEach( kv ->
        {
            serialize( root, kv );
        } );
    }

    private void serialize( Path root, Map.Entry<PackageElement,Collection<ExecutableElement>> kv )
    {
        PackageElement packageElement = kv.getKey();
        File file = new File( root.toFile(), packageElement.getQualifiedName() + ".csv" );

        Either<DsvExportError,List<String>> parsingResult =
                fieldExporter.exportHeaders( dsvConfiguration.getFieldDelimiter(), dsvConfiguration.getRawHeaders() );

        parsingResult.consume(
               messagePrinter::print,
                (headers) -> serialize( kv, file, headers )
        );
    }

    private void serialize( Map.Entry<PackageElement,Collection<ExecutableElement>> kv, File file,
            Collection<String> headers )
    {
        try ( FileWriter resource = new FileWriter( file );
                DsvWriter writer = new DsvWriter( headers, resource, dsvConfiguration.getFieldDelimiter() ) )
        {
            writer.write(
                    kv.getValue().stream(),
                    ( method ) -> serializeFields( method, headers ),
                    messagePrinter::print);
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e.getMessage(), e );
        }
    }

    private Stream<Either<DsvExportError,String>> serializeFields( ExecutableElement method, Collection<String> headers )
    {
        return fieldExporter.exportFields( method, headers );
    }

}
