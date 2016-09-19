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

import net.biville.florent.sproccompiler.compilerutils.TypeMirrorUtils;
import net.biville.florent.sproccompiler.messages.CompilationMessage;
import net.biville.florent.sproccompiler.messages.FunctionInRootNamespaceError;
import net.biville.florent.sproccompiler.messages.ReturnTypeError;
import net.biville.florent.sproccompiler.validators.AllowedTypesValidator;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.lang.model.util.Types;

import org.neo4j.procedure.UserFunction;

public class UserFunctionVisitor extends SimpleElementVisitor8<Stream<CompilationMessage>,Void>
{

    private final ElementVisitor<Stream<CompilationMessage>,Void> parameterVisitor;
    private final Predicate<TypeMirror> allowedTypesValidator;
    private final Elements elements;

    public UserFunctionVisitor( Types types, Elements elements, TypeMirrorUtils typeMirrorUtils )
    {
        this.parameterVisitor = new ParameterVisitor( new ParameterTypeVisitor( types, typeMirrorUtils ) );
        this.allowedTypesValidator = new AllowedTypesValidator( typeMirrorUtils, types );
        this.elements = elements;
    }

    @Override
    public Stream<CompilationMessage> visitExecutable( ExecutableElement method, Void ignored )
    {
        return Stream
                .concat( Stream.concat( validateParameters( method.getParameters(), ignored ), validateName( method ) ),
                        validateReturnType( method ) );
    }

    private Stream<CompilationMessage> validateParameters( List<? extends VariableElement> parameters, Void ignored )
    {
        return parameters.stream().flatMap( var -> parameterVisitor.visit( var, ignored ) );
    }

    private Stream<CompilationMessage> validateName( ExecutableElement method )
    {
        UserFunction function = method.getAnnotation( UserFunction.class );
        String name = function.name();
        if ( !name.isEmpty() && isInRootNamespace( name ) )
        {
            return Stream.of( rootNamespaceError( method, name ) );
        }
        String value = function.value();
        if ( !value.isEmpty() && isInRootNamespace( value ) )
        {
            return Stream.of( rootNamespaceError( method, value ) );
        }
        PackageElement namespace = elements.getPackageOf( method );
        if ( namespace == null )
        {
            return Stream.of( rootNamespaceError( method ) );
        }
        return Stream.empty();
    }

    private Stream<CompilationMessage> validateReturnType( ExecutableElement method )
    {
        TypeMirror returnType = method.getReturnType();
        if ( !allowedTypesValidator.test( returnType ) )
        {
            return Stream.of( new ReturnTypeError( method,
                    "Unsupported return type <%s> of function defined in " + "<%s#%s>.", returnType,
                    method.getEnclosingElement(), method.getSimpleName() ) );
        }
        return Stream.empty();
    }

    private boolean isInRootNamespace( String name )
    {
        return !name.contains( "." ) || name.split( "\\." )[0].isEmpty();
    }

    private FunctionInRootNamespaceError rootNamespaceError( ExecutableElement method, String name )
    {
        return new FunctionInRootNamespaceError( method,
                "Function <%s> cannot be defined in the root namespace. Valid name example: com.acme.my_function",
                name );
    }

    private FunctionInRootNamespaceError rootNamespaceError( ExecutableElement method )
    {
        return new FunctionInRootNamespaceError( method,
                "Function defined in <%s#%s> cannot be defined in the root namespace. " +
                        "Valid name example: com.acme.my_function", method.getEnclosingElement().getSimpleName(),
                method.getSimpleName() );
    }


}
