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

import net.biville.florent.sproccompiler.validators.AllowedTypesValidator;
import net.biville.florent.sproccompiler.compilerutils.TypeMirrorUtils;
import net.biville.florent.sproccompiler.errors.CompilationError;
import net.biville.florent.sproccompiler.errors.ParameterTypeError;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor8;
import javax.lang.model.util.Types;
import java.util.function.Predicate;
import java.util.stream.Stream;

class ParameterTypeVisitor extends SimpleTypeVisitor8<Stream<CompilationError>, VariableElement> {

    private final Predicate<TypeMirror> allowedTypesValidator;

    public ParameterTypeVisitor(Types typeUtils, TypeMirrorUtils typeMirrors) {
        allowedTypesValidator = new AllowedTypesValidator(typeMirrors, typeUtils);
    }

    @Override
    public Stream<CompilationError> visitDeclared(DeclaredType parameterType, VariableElement initialElement) {
        return validate(parameterType, initialElement);
    }

    @Override
    public Stream<CompilationError> visitPrimitive(PrimitiveType primitive, VariableElement initialElement) {
        return validate(primitive, initialElement);
    }

    @Override
    protected Stream<CompilationError> defaultAction(TypeMirror unknown, VariableElement initialElement) {
        return compilationError(initialElement);
    }

    private Stream<CompilationError> validate(TypeMirror typeMirror, VariableElement initialElement) {
        if (!allowedTypesValidator.test(typeMirror)) {
            return compilationError(initialElement);
        }
        return Stream.empty();
    }

    private Stream<CompilationError> compilationError(VariableElement initialElement) {
        Element method = initialElement.getEnclosingElement();
        return Stream.of(new ParameterTypeError(
                initialElement,
                "Unsupported parameter type <%s> of procedure %s#%s",
                initialElement.asType().toString(),
                method.getEnclosingElement().getSimpleName(),
                method.getSimpleName()
        ));
    }
}
