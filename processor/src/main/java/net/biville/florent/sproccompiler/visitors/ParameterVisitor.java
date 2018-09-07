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

import net.biville.florent.sproccompiler.messages.CompilationMessage;
import net.biville.florent.sproccompiler.messages.ParameterMissingAnnotationError;
import net.biville.florent.sproccompiler.messages.ParameterTypeError;

import java.util.List;
import java.util.stream.Stream;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.util.SimpleElementVisitor8;

import org.neo4j.procedure.Name;

class ParameterVisitor extends SimpleElementVisitor8<Stream<CompilationMessage>,Void>
{

    private final TypeVisitor<Boolean,Void> parameterTypeVisitor;

    public ParameterVisitor( TypeVisitor<Boolean,Void> parameterTypeVisitor )
    {
        this.parameterTypeVisitor = parameterTypeVisitor;
    }

    @Override
    public Stream<CompilationMessage> visitVariable( VariableElement parameter, Void ignored )
    {
        Name annotation = parameter.getAnnotation( Name.class );
        if ( annotation == null )
        {
            return Stream.of( new ParameterMissingAnnotationError( parameter,
                    annotationMirror( parameter.getAnnotationMirrors() ), "@%s usage error: missing on parameter <%s>",
                    Name.class.getName(), nameOf( parameter ) ) );
        }

        if ( !parameterTypeVisitor.visit( parameter.asType() ) )
        {
            Element method = parameter.getEnclosingElement();
            return Stream.of( new ParameterTypeError( parameter,
                    "Unsupported parameter type <%s> of " + "procedure|function" + " %s#%s",
                    parameter.asType().toString(), method.getEnclosingElement().getSimpleName(),
                    method.getSimpleName() ) );
        }
        return Stream.empty();
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
