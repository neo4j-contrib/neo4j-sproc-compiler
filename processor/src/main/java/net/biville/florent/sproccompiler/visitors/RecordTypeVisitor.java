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
import net.biville.florent.sproccompiler.errors.CompilationError;
import net.biville.florent.sproccompiler.errors.RecordTypeError;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.util.SimpleTypeVisitor8;
import javax.lang.model.util.Types;
import java.util.Set;
import java.util.stream.Stream;

import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.util.ElementFilter.fieldsIn;

class RecordTypeVisitor extends SimpleTypeVisitor8<Stream<CompilationError>, Void> {

    private final Types typeUtils;
    private final TypeVisitor<Boolean, Void> fieldTypeVisitor;

    public RecordTypeVisitor(Types typeUtils, TypeMirrorUtils typeMirrors) {
        this.typeUtils = typeUtils;
        fieldTypeVisitor = new RecordFieldTypeVisitor(typeUtils, typeMirrors);
    }

    @Override
    public Stream<CompilationError> visitDeclared(DeclaredType returnType, Void ignored) {
        return returnType.getTypeArguments()
                .stream()
                .flatMap(this::validateRecord);
    }

    private Stream<CompilationError> validateRecord(TypeMirror recordType) {
        Element recordElement = typeUtils.asElement(recordType);
        return Stream.concat(
                validateFieldModifiers(recordElement),
                validateFieldType(recordElement)
        );
    }

    private Stream<CompilationError> validateFieldModifiers(Element recordElement) {
        return fieldsIn(recordElement.getEnclosedElements())
                .stream()
                .filter(element -> {
                    Set<Modifier> modifiers = element.getModifiers();
                    return !modifiers.contains(PUBLIC) && !modifiers.contains(STATIC);
                })
                .map(element -> new RecordTypeError(
                        element,
                        "Record definition error: field %s#%s must be public",
                        recordElement.getSimpleName(),
                        element.getSimpleName()
                ));
    }

    private Stream<CompilationError> validateFieldType(Element recordElement) {
        return fieldsIn(recordElement.getEnclosedElements())
                .stream()
                .filter(element -> !element.getModifiers().contains(STATIC))
                .filter(element -> !fieldTypeVisitor.visit(element.asType()))
                .map(element -> new RecordTypeError(
                        element,
                        "Record definition error: type of field %s#%s is not supported",
                        recordElement.getSimpleName(),
                        element.getSimpleName()
                ));
    }

}
