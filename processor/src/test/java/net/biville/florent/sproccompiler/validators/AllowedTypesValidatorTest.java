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
import net.biville.florent.sproccompiler.TypeMirrorTestUtils;
import net.biville.florent.sproccompiler.compilerutils.TypeMirrorUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static javax.lang.model.type.TypeKind.*;
import static org.assertj.core.api.Assertions.assertThat;

public class AllowedTypesValidatorTest {

    @Rule public CompilationRule compilation = new CompilationRule();
    private TypeMirrorTestUtils typeMirrorTestUtils;
    private Predicate<TypeMirror> validator;

    @Before
    public void prepare() {
        Types types = compilation.getTypes();
        Elements elements = compilation.getElements();
        TypeMirrorUtils typeMirrors = new TypeMirrorUtils(types, elements);

        typeMirrorTestUtils = new TypeMirrorTestUtils(types, elements, typeMirrors);
        validator = new AllowedTypesValidator(typeMirrors, types);
    }

    @Test
    public void unsupported_simple_type_is_invalid() {
        assertThat(validator.test(typeMirrorTestUtils.typeOf(CharSequence.class))).isFalse();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(Thread.class))).isFalse();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(Character.class))).isFalse();
    }

    @Test
    public void supported_simple_type_is_valid() {
        assertThat(validator.test(typeMirrorTestUtils.typeOf(BOOLEAN))).isTrue();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(LONG))).isTrue();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(DOUBLE))).isTrue();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(Boolean.class))).isTrue();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(Long.class))).isTrue();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(Double.class))).isTrue();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(String.class))).isTrue();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(Number.class))).isTrue();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(Object.class))).isTrue();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(Node.class))).isTrue();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(Relationship.class))).isTrue();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(Path.class))).isTrue();
    }

    @Test
    public void supported_list_type_is_valid() {
        assertThat(validator.test(typeMirrorTestUtils.typeOf(List.class, Boolean.class))).isTrue();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(List.class, Long.class))).isTrue();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(List.class, Double.class))).isTrue();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(List.class, String.class))).isTrue();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(List.class, Number.class))).isTrue();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(List.class, Object.class))).isTrue();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(List.class, Node.class))).isTrue();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(List.class, Relationship.class))).isTrue();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(List.class, Path.class))).isTrue();
    }

    @Test
    public void unsupported_list_type_is_invalid() {
        assertThat(validator.test(typeMirrorTestUtils.typeOf(List.class, CharSequence.class))).isFalse();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(List.class, Thread.class))).isFalse();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(List.class, Character.class))).isFalse();
    }

    @Test
    public void supported_recursive_list_type_is_valid() {
        assertThat(validator.test(typeMirrorTestUtils.typeOf(List.class, typeMirrorTestUtils.typeOf(List.class, Boolean.class)))).isTrue();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(List.class, typeMirrorTestUtils.typeOf(List.class, typeMirrorTestUtils.typeOf(List.class, Object.class))))).isTrue();
    }

    @Test
    public void unsupported_recursive_list_type_is_invalid() {
        assertThat(validator.test(typeMirrorTestUtils.typeOf(List.class, typeMirrorTestUtils.typeOf(List.class, CharSequence.class)))).isFalse();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(List.class, typeMirrorTestUtils.typeOf(List.class, typeMirrorTestUtils.typeOf(List.class, Thread.class))))).isFalse();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(List.class, typeMirrorTestUtils.typeOf(List.class, typeMirrorTestUtils.typeOf(List.class, Character.class))))).isFalse();
    }

    @Test
    public void supported_map_type_is_valid() {
        assertThat(validator.test(typeMirrorTestUtils.typeOf(Map.class, String.class, Boolean.class))).isTrue();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(Map.class, String.class, Long.class))).isTrue();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(Map.class, String.class, Double.class))).isTrue();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(Map.class, String.class, String.class))).isTrue();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(Map.class, String.class, Number.class))).isTrue();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(Map.class, String.class, Object.class))).isTrue();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(Map.class, String.class, Node.class))).isTrue();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(Map.class, String.class, Relationship.class))).isTrue();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(Map.class, String.class, Path.class))).isTrue();
    }

    @Test
    public void supported_recursive_map_type_is_valid() {
        assertThat(validator.test(typeMirrorTestUtils.typeOf(Map.class,  typeMirrorTestUtils.typeOf(String.class), typeMirrorTestUtils.typeOf(Map.class, String.class, Boolean.class)))).isTrue();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(Map.class,  typeMirrorTestUtils.typeOf(String.class), typeMirrorTestUtils.typeOf(Map.class, typeMirrorTestUtils.typeOf(String.class), typeMirrorTestUtils.typeOf(Map.class, String.class, Boolean.class))))).isTrue();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(List.class, typeMirrorTestUtils.typeOf(List.class,    typeMirrorTestUtils.typeOf(Map.class, String.class, Boolean.class))))).isTrue();
    }

    @Test
    public void unsupported_recursive_map_type_is_invalid() {
        assertThat(validator.test(typeMirrorTestUtils.typeOf(Map.class,  typeMirrorTestUtils.typeOf(String.class), typeMirrorTestUtils.typeOf(Map.class, String.class, Thread.class)))).isFalse();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(Map.class,  typeMirrorTestUtils.typeOf(String.class), typeMirrorTestUtils.typeOf(Map.class, typeMirrorTestUtils.typeOf(String.class), typeMirrorTestUtils.typeOf(Map.class, String.class, CharSequence.class))))).isFalse();
        assertThat(validator.test(typeMirrorTestUtils.typeOf(List.class, typeMirrorTestUtils.typeOf(List.class,    typeMirrorTestUtils.typeOf(Map.class, String.class, Character.class))))).isFalse();
    }



}