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
import net.biville.florent.sproccompiler.compilerutils.TypeMirrorUtils;
import net.biville.florent.sproccompiler.messages.CompilationMessage;
import net.biville.florent.sproccompiler.messages.MessagePrinter;
import net.biville.florent.sproccompiler.validators.DuplicatedProcedureValidator;
import net.biville.florent.sproccompiler.visitors.UserFunctionVisitor;

import java.util.Collection;
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

import org.neo4j.procedure.UserFunction;

@AutoService( Processor.class )
public class UserFunctionProcessor extends AbstractProcessor
{
    private static final Class<UserFunction> userFunctionType = UserFunction.class;
    private final Set<Element> visitedFunctions = new LinkedHashSet<>();

    private ElementVisitor<Stream<CompilationMessage>,Void> visitor;
    private MessagePrinter messagePrinter;
    private Function<Collection<Element>,Stream<CompilationMessage>> duplicationPredicate;

    public static Optional<String> getCustomName( UserFunction function )
    {
        String name = function.name();
        if ( !name.isEmpty() )
        {
            return Optional.of( name );
        }
        String value = function.value();
        if ( !value.isEmpty() )
        {
            return Optional.of( value );
        }
        return Optional.empty();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes()
    {
        Set<String> types = new HashSet<>();
        types.add( userFunctionType.getName() );
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

        visitedFunctions.clear();
        messagePrinter = new MessagePrinter( processingEnv.getMessager() );
        visitor = new UserFunctionVisitor( typeUtils, elementUtils, new TypeMirrorUtils( typeUtils, elementUtils ) );
        duplicationPredicate = new DuplicatedProcedureValidator<>( elementUtils, userFunctionType,
                UserFunctionProcessor::getCustomName );
    }

    @Override
    public boolean process( Set<? extends TypeElement> annotations, RoundEnvironment roundEnv )
    {
        processElements( roundEnv );
        if ( roundEnv.processingOver() )
        {
            duplicationPredicate.apply( visitedFunctions ).forEach( messagePrinter::print );
        }
        return false;
    }

    private void processElements( RoundEnvironment roundEnv )
    {
        Set<? extends Element> functions = roundEnv.getElementsAnnotatedWith( userFunctionType );
        visitedFunctions.addAll( functions );
        functions.stream().flatMap( this::validate ).forEachOrdered( messagePrinter::print );
    }

    private Stream<CompilationMessage> validate( Element element )
    {
        return visitor.visit( element );
    }
}
