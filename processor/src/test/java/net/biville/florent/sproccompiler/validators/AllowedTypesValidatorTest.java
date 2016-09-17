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
package net.biville.florent.sproccompiler.validators;

import com.google.testing.compile.CompilationRule;
import net.biville.florent.sproccompiler.compilerutils.TypeMirrors;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;

import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static javax.lang.model.type.TypeKind.*;
import static org.assertj.core.api.Assertions.assertThat;

public class AllowedTypesValidatorTest {

    @Rule public CompilationRule compilation = new CompilationRule();
    private Types types;
    private Elements elements;
    private TypeMirrors typeMirrors;
    private Predicate<TypeMirror> validator;

    @Before
    public void prepare() {
        types = compilation.getTypes();
        elements = compilation.getElements();
        typeMirrors = new TypeMirrors(types, elements);
        validator = new AllowedTypesValidator(typeMirrors, types);
    }

    @Test
    public void unsupported_simple_type_is_invalid() {
        assertThat(validator.test(typeOf(CharSequence.class))).isFalse();
        assertThat(validator.test(typeOf(Thread.class))).isFalse();
        assertThat(validator.test(typeOf(Character.class))).isFalse();
    }

    @Test
    public void supported_simple_type_is_valid() {
        assertThat(validator.test(typeOf(BOOLEAN))).isTrue();
        assertThat(validator.test(typeOf(LONG))).isTrue();
        assertThat(validator.test(typeOf(DOUBLE))).isTrue();
        assertThat(validator.test(typeOf(Boolean.class))).isTrue();
        assertThat(validator.test(typeOf(Long.class))).isTrue();
        assertThat(validator.test(typeOf(Double.class))).isTrue();
        assertThat(validator.test(typeOf(String.class))).isTrue();
        assertThat(validator.test(typeOf(Number.class))).isTrue();
        assertThat(validator.test(typeOf(Object.class))).isTrue();
        assertThat(validator.test(typeOf(Node.class))).isTrue();
        assertThat(validator.test(typeOf(Relationship.class))).isTrue();
        assertThat(validator.test(typeOf(Path.class))).isTrue();
    }

    @Test
    public void supported_list_type_is_valid() {
        assertThat(validator.test(typeOf(List.class, Boolean.class))).isTrue();
        assertThat(validator.test(typeOf(List.class, Long.class))).isTrue();
        assertThat(validator.test(typeOf(List.class, Double.class))).isTrue();
        assertThat(validator.test(typeOf(List.class, String.class))).isTrue();
        assertThat(validator.test(typeOf(List.class, Number.class))).isTrue();
        assertThat(validator.test(typeOf(List.class, Object.class))).isTrue();
        assertThat(validator.test(typeOf(List.class, Node.class))).isTrue();
        assertThat(validator.test(typeOf(List.class, Relationship.class))).isTrue();
        assertThat(validator.test(typeOf(List.class, Path.class))).isTrue();
    }

    @Test
    public void unsupported_list_type_is_invalid() {
        assertThat(validator.test(typeOf(List.class, CharSequence.class))).isFalse();
        assertThat(validator.test(typeOf(List.class, Thread.class))).isFalse();
        assertThat(validator.test(typeOf(List.class, Character.class))).isFalse();
    }

    @Test
    public void supported_recursive_list_type_is_valid() {
        assertThat(validator.test(typeOf(List.class, typeOf(List.class, Boolean.class)))).isTrue();
        assertThat(validator.test(typeOf(List.class, typeOf(List.class, typeOf(List.class, Object.class))))).isTrue();
    }

    @Test
    public void unsupported_recursive_list_type_is_invalid() {
        assertThat(validator.test(typeOf(List.class, typeOf(List.class, CharSequence.class)))).isFalse();
        assertThat(validator.test(typeOf(List.class, typeOf(List.class, typeOf(List.class, Thread.class))))).isFalse();
        assertThat(validator.test(typeOf(List.class, typeOf(List.class, typeOf(List.class, Character.class))))).isFalse();
    }

    @Test
    public void supported_map_type_is_valid() {
        assertThat(validator.test(typeOf(Map.class, String.class, Boolean.class))).isTrue();
        assertThat(validator.test(typeOf(Map.class, String.class, Long.class))).isTrue();
        assertThat(validator.test(typeOf(Map.class, String.class, Double.class))).isTrue();
        assertThat(validator.test(typeOf(Map.class, String.class, String.class))).isTrue();
        assertThat(validator.test(typeOf(Map.class, String.class, Number.class))).isTrue();
        assertThat(validator.test(typeOf(Map.class, String.class, Object.class))).isTrue();
        assertThat(validator.test(typeOf(Map.class, String.class, Node.class))).isTrue();
        assertThat(validator.test(typeOf(Map.class, String.class, Relationship.class))).isTrue();
        assertThat(validator.test(typeOf(Map.class, String.class, Path.class))).isTrue();
    }

    @Test
    public void supported_recursive_map_type_is_valid() {
        assertThat(validator.test(typeOf(Map.class,  typeOf(String.class), typeOf(Map.class, String.class, Boolean.class)))).isTrue();
        assertThat(validator.test(typeOf(Map.class,  typeOf(String.class), typeOf(Map.class, typeOf(String.class), typeOf(Map.class, String.class, Boolean.class))))).isTrue();
        assertThat(validator.test(typeOf(List.class, typeOf(List.class,    typeOf(Map.class, String.class, Boolean.class))))).isTrue();
    }

    @Test
    public void unsupported_recursive_map_type_is_invalid() {
        assertThat(validator.test(typeOf(Map.class,  typeOf(String.class), typeOf(Map.class, String.class, Thread.class)))).isFalse();
        assertThat(validator.test(typeOf(Map.class,  typeOf(String.class), typeOf(Map.class, typeOf(String.class), typeOf(Map.class, String.class, CharSequence.class))))).isFalse();
        assertThat(validator.test(typeOf(List.class, typeOf(List.class,    typeOf(Map.class, String.class, Character.class))))).isFalse();
    }

    private TypeMirror typeOf(Class<?> type, Class<?>... parameterTypes) {
        return types.getDeclaredType(
            elements.getTypeElement(type.getName()),
            typesOf(parameterTypes)
        );
    }

    private TypeMirror typeOf(Class<?> type, TypeMirror... parameterTypes) {
        return types.getDeclaredType(
            elements.getTypeElement(type.getName()),
            parameterTypes
        );
    }

    private TypeMirror[] typesOf(Class<?>... parameterTypes) {
        Stream<TypeMirror> mirrorStream = stream(parameterTypes).map(this::typeOf);
        return mirrorStream.collect(toList()).toArray(new TypeMirror[parameterTypes.length]);
    }

    private PrimitiveType typeOf(TypeKind aDouble) {
        return typeMirrors.primitive(aDouble);
    }

    private TypeMirror typeOf(Class<?> type) {
        return typeMirrors.typeMirror(type);
    }

}