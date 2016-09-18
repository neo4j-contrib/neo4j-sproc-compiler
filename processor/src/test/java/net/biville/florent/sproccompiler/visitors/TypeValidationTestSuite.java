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

import net.biville.florent.sproccompiler.testutils.TypeMirrorTestUtils;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeVisitor;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;

import static org.assertj.core.api.Assertions.assertThat;

abstract class TypeValidationTestSuite
{

    @Test
    public void validates_supported_simple_types()
    {
        assertThat( visitor().visit( typeMirrorTestUtils().typeOf( String.class ) ) ).isTrue();
        assertThat( visitor().visit( typeMirrorTestUtils().typeOf( Number.class ) ) ).isTrue();
        assertThat( visitor().visit( typeMirrorTestUtils().typeOf( Long.class ) ) ).isTrue();
        assertThat( visitor().visit( typeMirrorTestUtils().typeOf( TypeKind.LONG ) ) ).isTrue();
        assertThat( visitor().visit( typeMirrorTestUtils().typeOf( Double.class ) ) ).isTrue();
        assertThat( visitor().visit( typeMirrorTestUtils().typeOf( TypeKind.DOUBLE ) ) ).isTrue();
        assertThat( visitor().visit( typeMirrorTestUtils().typeOf( Boolean.class ) ) ).isTrue();
        assertThat( visitor().visit( typeMirrorTestUtils().typeOf( TypeKind.BOOLEAN ) ) ).isTrue();
        assertThat( visitor().visit( typeMirrorTestUtils().typeOf( Path.class ) ) ).isTrue();
        assertThat( visitor().visit( typeMirrorTestUtils().typeOf( Node.class ) ) ).isTrue();
        assertThat( visitor().visit( typeMirrorTestUtils().typeOf( Relationship.class ) ) ).isTrue();
        assertThat( visitor().visit( typeMirrorTestUtils().typeOf( Object.class ) ) ).isTrue();
    }

    @Test
    public void validates_supported_generic_types()
    {
        assertThat( visitor().visit( typeMirrorTestUtils().typeOf( Map.class, String.class, Object.class ) ) ).isTrue();
        assertThat( visitor().visit( typeMirrorTestUtils().typeOf( HashMap.class, String.class, Object.class ) ) )
                .isTrue();
        assertThat( visitor().visit( typeMirrorTestUtils().typeOf( LinkedHashMap.class, String.class, Object.class ) ) )
                .isTrue();
        assertThat( visitor().visit( typeMirrorTestUtils().typeOf( List.class, String.class ) ) ).isTrue();
        assertThat( visitor().visit( typeMirrorTestUtils().typeOf( LinkedList.class, Number.class ) ) ).isTrue();
        assertThat( visitor().visit( typeMirrorTestUtils().typeOf( ArrayList.class, Long.class ) ) ).isTrue();
        assertThat( visitor().visit( typeMirrorTestUtils().typeOf( List.class, Double.class ) ) ).isTrue();
        assertThat( visitor().visit( typeMirrorTestUtils().typeOf( List.class, Boolean.class ) ) ).isTrue();
        assertThat( visitor().visit( typeMirrorTestUtils().typeOf( List.class, Path.class ) ) ).isTrue();
        assertThat( visitor().visit( typeMirrorTestUtils().typeOf( List.class, Node.class ) ) ).isTrue();
        assertThat( visitor().visit( typeMirrorTestUtils().typeOf( List.class, Relationship.class ) ) ).isTrue();
        assertThat( visitor().visit( typeMirrorTestUtils().typeOf( List.class, Object.class ) ) ).isTrue();
        assertThat( visitor().visit( typeMirrorTestUtils()
                .typeOf( List.class, typeMirrorTestUtils().typeOf( Map.class, String.class, Object.class ) ) ) )
                .isTrue();
        assertThat( visitor().visit( typeMirrorTestUtils()
                .typeOf( List.class, typeMirrorTestUtils().typeOf( LinkedList.class, Long.class ) ) ) ).isTrue();
    }

    @Test
    public void rejects_unsupported_types()
    {
        assertThat( visitor().visit( typeMirrorTestUtils().typeOf( Thread.class ) ) ).isFalse();
        assertThat( visitor().visit( typeMirrorTestUtils().typeOf( Map.class, String.class, Integer.class ) ) )
                .isFalse();
        assertThat( visitor().visit( typeMirrorTestUtils().typeOf( Map.class, Integer.class, Object.class ) ) )
                .isFalse();
        assertThat( visitor().visit( typeMirrorTestUtils().typeOf( Map.class, Integer.class, Integer.class ) ) )
                .isFalse();
        assertThat( visitor().visit( typeMirrorTestUtils().typeOf( List.class, BigDecimal.class ) ) ).isFalse();
        assertThat( visitor().visit( typeMirrorTestUtils()
                .typeOf( List.class, typeMirrorTestUtils().typeOf( Map.class, String.class, Integer.class ) ) ) )
                .isFalse();
        assertThat( visitor().visit( typeMirrorTestUtils()
                .typeOf( List.class, typeMirrorTestUtils().typeOf( List.class, CharSequence.class ) ) ) ).isFalse();
    }

    protected abstract TypeVisitor<Boolean,Void> visitor();

    protected abstract TypeMirrorTestUtils typeMirrorTestUtils();

}
