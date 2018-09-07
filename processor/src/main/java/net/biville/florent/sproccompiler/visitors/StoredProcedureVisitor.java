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
package net.biville.florent.sproccompiler.visitors;

import net.biville.florent.sproccompiler.compilerutils.TypeMirrorUtils;
import net.biville.florent.sproccompiler.messages.CompilationMessage;
import net.biville.florent.sproccompiler.messages.ReturnTypeError;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.lang.model.util.Types;

import org.neo4j.procedure.Name;

public class StoredProcedureVisitor extends SimpleElementVisitor8<Stream<CompilationMessage>,Void>
{

    private final Types typeUtils;
    private final Elements elementUtils;
    private final ElementVisitor<Stream<CompilationMessage>,Void> classVisitor;
    private final TypeVisitor<Stream<CompilationMessage>,Void> recordVisitor;
    private final ElementVisitor<Stream<CompilationMessage>,Void> parameterVisitor;

    public StoredProcedureVisitor( Types typeUtils, Elements elementUtils, boolean skipContextWarnings )
    {
        TypeMirrorUtils typeMirrors = new TypeMirrorUtils( typeUtils, elementUtils );

        this.typeUtils = typeUtils;
        this.elementUtils = elementUtils;
        this.classVisitor = new StoredProcedureClassVisitor( typeUtils, elementUtils, skipContextWarnings );
        this.recordVisitor = new RecordTypeVisitor( typeUtils, typeMirrors );
        this.parameterVisitor = new ParameterVisitor( new ParameterTypeVisitor( typeUtils, typeMirrors ) );
    }

    /**
     * Validates method parameters and return type
     */
    @Override
    public Stream<CompilationMessage> visitExecutable( ExecutableElement executableElement, Void ignored )
    {
        return Stream.of( classVisitor.visit( executableElement.getEnclosingElement() ),
                validateParameters( executableElement.getParameters(), ignored ),
                validateReturnType( executableElement ) ).flatMap( Function.identity() );
    }

    private Stream<CompilationMessage> validateParameters( List<? extends VariableElement> parameters, Void ignored )
    {
        return parameters.stream().flatMap( var -> parameterVisitor.visit( var, ignored ) );
    }

    private Stream<CompilationMessage> validateReturnType( ExecutableElement method )
    {
        String streamClassName = Stream.class.getCanonicalName();

        TypeMirror streamType = typeUtils.erasure( elementUtils.getTypeElement( streamClassName ).asType() );
        TypeMirror returnType = method.getReturnType();
        TypeMirror erasedReturnType = typeUtils.erasure( returnType );

        TypeMirror voidType = typeUtils.getNoType( TypeKind.VOID );
        if ( typeUtils.isSameType( returnType, voidType ) )
        {
            return Stream.empty();
        }

        if ( !typeUtils.isSubtype( erasedReturnType, streamType ) )
        {
            return Stream.of( new ReturnTypeError( method, "Return type of %s#%s must be %s",
                    method.getEnclosingElement().getSimpleName(), method.getSimpleName(), streamClassName ) );
        }

        return recordVisitor.visit( returnType );
    }

    private AnnotationMirror annotationMirror( List<? extends AnnotationMirror> mirrors )
    {
        AnnotationTypeVisitor nameVisitor = new AnnotationTypeVisitor( Name.class );
        return mirrors.stream().filter( mirror -> nameVisitor.visit( mirror.getAnnotationType().asElement() ) )
                .findFirst().orElse( null );
    }

    private String nameOf( VariableElement parameter )
    {
        return parameter.getSimpleName().toString();
    }
}
