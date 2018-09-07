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

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import org.neo4j.procedure.Procedure;
import org.neo4j.procedure.UserFunction;

import static java.util.Arrays.asList;

@AutoService( Processor.class )
public class DsvProcessor extends AbstractProcessor
{

    private final Collection<ExecutableElement> visitedProcedures = new TreeSet<>( Comparator.comparing( o -> o.getSimpleName().toString() ) );
    private final Collection<ExecutableElement> visitedFunctions = new TreeSet<>(Comparator.comparing( o -> o.getSimpleName().toString() ));

    private ExecutableElementVisitor methodVisitor;
    private DsvConfiguration configuration;
    private DsvSerializer serializer;
    private Messager messager;

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

        messager = processingEnv.getMessager();
        methodVisitor = new ExecutableElementVisitor( messager );
        configuration = new DsvConfiguration( processingEnv.getOptions() );
        serializer = new DsvSerializer( processingEnv.getElementUtils(), new TypeElementVisitor( messager ),
                new MessagePrinter( messager ) );
    }

    @Override
    public boolean process( Set<? extends TypeElement> annotations, RoundEnvironment roundEnv )
    {
        Optional<Path> rootPath = configuration.getRootPath();
        if ( !rootPath.isPresent() )
        {
            if ( roundEnv.processingOver() )
            {
                messager.printMessage( Diagnostic.Kind.WARNING,
                        String.format( "Skipping export, export path option -A%s not specified",
                                DsvConfiguration.DOCUMENTATION_ROOT_PATH ) );
            }

            return false;
        }

        processSources( roundEnv, rootPath.get() );

        return false;
    }

    private void processSources( RoundEnvironment roundEnv, Path root )
    {
        if ( roundEnv.processingOver() )
        {
            checkExportFolder( root );
            serializer.serialize( root, configuration, visitedProcedures, visitedFunctions );
        }
        else
        {
            roundEnv.getElementsAnnotatedWith( Procedure.class ).forEach( this::visitProcedure );
            roundEnv.getElementsAnnotatedWith( UserFunction.class ).forEach( this::visitFunction );
        }
    }

    private void visitFunction( Element el )
    {
        visitedFunctions.add( methodVisitor.visit( el ) );
    }

    private void visitProcedure( Element el )
    {
        visitedProcedures.add( methodVisitor.visit( el ) );
    }

    private static void checkExportFolder( Path root )
    {
        File rootFolder = root.toFile();
        String absolutePath = rootFolder.getAbsolutePath();
        if ( !rootFolder.exists() && !rootFolder.mkdirs() )
        {
            throw new IllegalArgumentException(
                    String.format( "Could not create export path <%s>!", absolutePath ) );
        }
        if ( !rootFolder.isDirectory() || !rootFolder.canWrite() )
        {
            throw new IllegalArgumentException(
                    String.format( "Export path <%s> should be a writable directory", absolutePath ) );
        }
    }


}
