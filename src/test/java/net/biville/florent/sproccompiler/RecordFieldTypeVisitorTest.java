package net.biville.florent.sproccompiler;

import com.google.testing.compile.CompilationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.math.BigDecimal;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class RecordFieldTypeVisitorTest {

    @Rule public CompilationRule compilationRule = new CompilationRule();
    private TypeVisitor<Boolean, Void> visitor;
    private Elements elementUtils;
    private Types typeUtils;

    @Before
    public void prepare() {
        elementUtils = compilationRule.getElements();
        typeUtils = compilationRule.getTypes();
        visitor = new RecordFieldTypeVisitor(typeUtils, elementUtils);
    }

    @Test
    public void validates_supported_simple_types() {
        assertThat(visitor.visit(typeMirror(String.class))).isTrue();
        assertThat(visitor.visit(typeMirror(Number.class))).isTrue();
        assertThat(visitor.visit(typeMirror(Long.class))).isTrue();
        assertThat(visitor.visit(primitive(TypeKind.LONG))).isTrue();
        assertThat(visitor.visit(typeMirror(Double.class))).isTrue();
        assertThat(visitor.visit(primitive(TypeKind.DOUBLE))).isTrue();
        assertThat(visitor.visit(typeMirror(Boolean.class))).isTrue();
        assertThat(visitor.visit(primitive(TypeKind.BOOLEAN))).isTrue();
        assertThat(visitor.visit(typeMirror(Path.class))).isTrue();
        assertThat(visitor.visit(typeMirror(Node.class))).isTrue();
        assertThat(visitor.visit(typeMirror(Relationship.class))).isTrue();
        assertThat(visitor.visit(typeMirror(Object.class))).isTrue();
    }

    @Test
    public void validates_supported_generic_types() {
        assertThat(visitor.visit(genericSimpleTypeMirror(Map.class, asList(String.class, Object.class)))).isTrue();
        assertThat(visitor.visit(genericSimpleTypeMirror(HashMap.class, asList(String.class, Object.class)))).isTrue();
        assertThat(visitor.visit(genericSimpleTypeMirror(LinkedHashMap.class, asList(String.class, Object.class)))).isTrue();
        assertThat(visitor.visit(genericSimpleTypeMirror(List.class, String.class))).isTrue();
        assertThat(visitor.visit(genericSimpleTypeMirror(LinkedList.class, Number.class))).isTrue();
        assertThat(visitor.visit(genericSimpleTypeMirror(ArrayList.class, Long.class))).isTrue();
        assertThat(visitor.visit(genericSimpleTypeMirror(List.class, Double.class))).isTrue();
        assertThat(visitor.visit(genericSimpleTypeMirror(List.class, Boolean.class))).isTrue();
        assertThat(visitor.visit(genericSimpleTypeMirror(List.class, Path.class))).isTrue();
        assertThat(visitor.visit(genericSimpleTypeMirror(List.class, Node.class))).isTrue();
        assertThat(visitor.visit(genericSimpleTypeMirror(List.class, Relationship.class))).isTrue();
        assertThat(visitor.visit(genericSimpleTypeMirror(List.class, Object.class))).isTrue();
        assertThat(visitor.visit(genericCompoundTypeMirror(
                List.class, // List<Map<String,Object>>
                genericSimpleTypeMirror(Map.class, asList(String.class, Object.class))))).isTrue();
        assertThat(visitor.visit(genericCompoundTypeMirror(
                List.class, // List<LinkedList<Long>>
                genericSimpleTypeMirror(LinkedList.class, Long.class)))).isTrue();
    }

    @Test
    public void rejects_unsupported_types() {
        assertThat(visitor.visit(typeMirror(Thread.class))).isFalse();
        assertThat(visitor.visit(genericSimpleTypeMirror(Map.class, asList(String.class, Integer.class)))).isFalse();
        assertThat(visitor.visit(genericSimpleTypeMirror(Map.class, asList(Integer.class, Object.class)))).isFalse();
        assertThat(visitor.visit(genericSimpleTypeMirror(Map.class, asList(Integer.class, Integer.class)))).isFalse();
        assertThat(visitor.visit(genericSimpleTypeMirror(List.class, BigDecimal.class))).isFalse();
        assertThat(visitor.visit(genericCompoundTypeMirror(
                List.class, // List<Map<String,Integer>>
                genericSimpleTypeMirror(Map.class, asList(String.class, Integer.class))))).isFalse();
        assertThat(visitor.visit(genericCompoundTypeMirror(
                List.class, // List<List<CharSequence>>
                genericSimpleTypeMirror(List.class, CharSequence.class)))).isFalse();
    }

    private DeclaredType genericSimpleTypeMirror(Class<?> mainType, Class<?> parameterType) {
        return genericSimpleTypeMirror(mainType, Collections.singletonList(parameterType));
    }

    private DeclaredType genericCompoundTypeMirror(Class<?> mainType, TypeMirror parameterType) {
        return genericCompoundTypeMirror(mainType, Collections.singletonList(parameterType));
    }

    private DeclaredType genericSimpleTypeMirror(Class<?> mainType, Collection<Class<?>> parameterTypes) {
        return genericCompoundTypeMirror(
                mainType,
                parameterTypes.stream().map(this::typeMirror).collect(toList())
        );
    }

    private DeclaredType genericCompoundTypeMirror(Class<?> mainType, Collection<TypeMirror> parameterTypes) {
        return typeUtils.getDeclaredType(
                elementUtils.getTypeElement(mainType.getCanonicalName()),
                parameterTypes.toArray(new TypeMirror[0])
        );
    }

    private TypeMirror typeMirror(Class<?> type) {
        return elementUtils.getTypeElement(type.getTypeName()).asType();
    }

    private TypeMirror primitive(TypeKind type) {
        return typeUtils.getPrimitiveType(type);
    }
}