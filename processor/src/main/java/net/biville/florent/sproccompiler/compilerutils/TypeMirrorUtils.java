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
package net.biville.florent.sproccompiler.compilerutils;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;

import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class TypeMirrorUtils {

    private Types typeUtils;
    private Elements elementUtils;

    public TypeMirrorUtils(Types typeUtils, Elements elementUtils) {
        this.typeUtils = typeUtils;
        this.elementUtils = elementUtils;
    }

    public final Collection<TypeMirror> procedureAllowedTypes() {
        PrimitiveType bool = primitive(TypeKind.BOOLEAN);
        PrimitiveType longType = primitive(TypeKind.LONG);
        PrimitiveType doubleType = primitive(TypeKind.DOUBLE);
        return asList(
            bool, boxed(bool),
            longType, boxed(longType),
            doubleType, boxed(doubleType),
            typeMirror(String.class),
            typeMirror(Number.class),
            typeMirror(Object.class),
            typeMirror(Map.class),
            typeMirror(List.class),
            typeMirror(Node.class),
            typeMirror(Relationship.class),
            typeMirror(Path.class)
        );
    }

    public PrimitiveType primitive(TypeKind kind) {
        return typeUtils.getPrimitiveType(kind);
    }

    public TypeMirror boxed(PrimitiveType bool) {
        return typeUtils.boxedClass(bool).asType();
    }

    public TypeMirror typeMirror(Class<?> type) {
        return elementUtils.getTypeElement(type.getName()).asType();
    }
}
