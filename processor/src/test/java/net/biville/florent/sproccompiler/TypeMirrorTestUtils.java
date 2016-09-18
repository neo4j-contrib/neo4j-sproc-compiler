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
package net.biville.florent.sproccompiler;

import net.biville.florent.sproccompiler.compilerutils.TypeMirrorUtils;

import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public class TypeMirrorTestUtils {

    private final Types types;
    private final Elements elements;
    private final TypeMirrorUtils typeMirrors;

    public TypeMirrorTestUtils(Types types, Elements elements, TypeMirrorUtils typeMirrors) {
        this.types = types;
        this.elements = elements;
        this.typeMirrors = typeMirrors;
    }

    public TypeMirror typeOf(Class<?> type, Class<?>... parameterTypes) {
        return types.getDeclaredType(
                elements.getTypeElement(type.getName()),
                typesOf(parameterTypes)
        );
    }

    public TypeMirror typeOf(Class<?> type, TypeMirror... parameterTypes) {
        return types.getDeclaredType(
                elements.getTypeElement(type.getName()),
                parameterTypes
        );
    }

    public PrimitiveType typeOf(TypeKind kind) {
        return typeMirrors.primitive(kind);
    }

    public TypeMirror typeOf(Class<?> type) {
        return typeMirrors.typeMirror(type);
    }

    private TypeMirror[] typesOf(Class<?>... parameterTypes) {
        Stream<TypeMirror> mirrorStream = stream(parameterTypes).map(this::typeOf);
        return mirrorStream.collect(toList()).toArray(new TypeMirror[parameterTypes.length]);
    }
}
