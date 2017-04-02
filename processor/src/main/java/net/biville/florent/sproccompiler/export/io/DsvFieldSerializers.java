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
package net.biville.florent.sproccompiler.export.io;

import net.biville.florent.sproccompiler.ProcedureProcessor;
import net.biville.florent.sproccompiler.UserFunctionProcessor;
import net.biville.florent.sproccompiler.export.Either;
import net.biville.florent.sproccompiler.export.messages.DsvExportError;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.PerformsWrites;
import org.neo4j.procedure.Procedure;
import org.neo4j.procedure.UserFunction;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleTypeVisitor8;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * All possible DSV header values: this declaration order is the default one
 */
public class DsvFieldSerializers
{

    private final Map<String,Function<ExecutableElement,Either<DsvExportError,String>>> headerSerializers =
            new LinkedHashMap<>( 7 );

    DsvFieldSerializers( Elements elementUtils )
    {
        FieldSerializers fieldSerializers = new FieldSerializers( elementUtils );
        headerSerializers.put( "type", fieldSerializers::type );
        headerSerializers.put( "qualified name", fieldSerializers::qualifiedName );
        headerSerializers.put( "signature", fieldSerializers::signature );
        headerSerializers.put( "description", fieldSerializers::description );
        headerSerializers.put( "execution mode", fieldSerializers::executionMode );
        headerSerializers.put( "location", fieldSerializers::location );
        headerSerializers.put( "deprecated by", fieldSerializers::deprecatedBy );
    }

    public List<String> getAllFields()
    {
        List<String> result = new ArrayList<>( headerSerializers.size() );
        result.addAll( headerSerializers.keySet() );
        return result;
    }

    public Either<DsvExportError,String> serializeField( ExecutableElement method, String field )
    {
        return headerSerializers.getOrDefault( field,
                ( ignored ) -> Either.left( new DsvExportError( method, "Unsupported field name: " + field ) ) )
                .apply( method );
    }

    private static class FieldSerializers
    {

        private final Elements elementUtils;

        public FieldSerializers( Elements elementUtils )
        {
            this.elementUtils = elementUtils;
        }

        public Either<DsvExportError,String> type( ExecutableElement method )
        {
            if ( method.getAnnotation( UserFunction.class ) != null )
            {
                return Either.right( "function" );
            }
            if ( method.getAnnotation( Procedure.class ) != null )
            {
                return Either.right( "procedure" );
            }
            return Either.left( new DsvExportError( method,
                    "Method %s is neither annotated with @UserFunction or @Procedure. Exiting now...", method ) );
        }

        public Either<DsvExportError,String> qualifiedName(ExecutableElement method )
        {
            return Either.right( callableName( method ) );
        }

        public Either<DsvExportError,String> signature(ExecutableElement method )
        {
            return Either.right(String.format("%s %s(%s)", returnType(method), method.getSimpleName(), parameters(method)));
        }

        public Either<DsvExportError,String> description( ExecutableElement method )
        {
            Description description = method.getAnnotation( Description.class );
            if ( description == null )
            {
                return Either.right( "" );
            }
            return Either.right( description.value() );
        }

        public Either<DsvExportError,String> executionMode( ExecutableElement method )
        {
            PerformsWrites performsWrites = method.getAnnotation( PerformsWrites.class );
            if ( performsWrites != null )
            {
                return Either.right( "PERFORMS_WRITE" );
            }
            Procedure procedure = method.getAnnotation( Procedure.class );
            if ( procedure != null )
            {
                return Either.right( procedure.mode().name() );
            }
            return Either.right( "" );
        }

        public Either<DsvExportError,String> location( ExecutableElement method )
        {
            return Either.right( String.format( "%s.%s", elementUtils.getPackageOf( method ).getQualifiedName(),
                    method.getEnclosingElement().getSimpleName() ) );
        }

        public Either<DsvExportError,String> deprecatedBy( ExecutableElement method )
        {
            UserFunction function = method.getAnnotation( UserFunction.class );
            if ( function != null )
            {
                return Either.right( function.deprecatedBy() );
            }
            return Either.right( method.getAnnotation( Procedure.class ).deprecatedBy() );
        }

        private String returnType(ExecutableElement method) {
            return method.getReturnType().toString();
        }

        private String callableName( ExecutableElement method )
        {
            Supplier<String> defaultName =
                    () -> elementUtils.getPackageOf( method ).getQualifiedName() + "." + method.getSimpleName();
            UserFunction function = method.getAnnotation( UserFunction.class );
            if ( function != null )
            {
                return UserFunctionProcessor.getCustomName( function ).orElseGet( defaultName );
            }
            Procedure procedure = method.getAnnotation( Procedure.class );
            return ProcedureProcessor.getCustomName( procedure ).orElseGet( defaultName );
        }

        private String parameters( ExecutableElement method )
        {
            return method.getParameters().stream().map( this::parameterSignature ).collect( Collectors.joining( "," ) );
        }

        private String parameterSignature( VariableElement param )
        {
            return getSimpleTypeName( param ) + " " + param.getAnnotation( org.neo4j.procedure.Name.class ).value();
        }

        private String getSimpleTypeName( VariableElement param )
        {
            return new SimpleTypeVisitor8<String,Void>()
            {
                @Override
                public String visitPrimitive( PrimitiveType t, Void aVoid )
                {
                    return t.toString();
                }

                @Override
                public String visitDeclared( DeclaredType t, Void aVoid )
                {
                    return t.asElement().getSimpleName().toString();
                }
            }.visit( param.asType() );
        }
    }
}
