package io.github.fbiville.sproccompiler;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleTypeVisitor8;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

class ParameterTypeVisitor extends SimpleTypeVisitor8<Stream<CompilationError>, VariableElement> {

    private final Types typeUtils;
    private final Elements elementUtils;

    public ParameterTypeVisitor(Types typeUtils, Elements elementUtils) {
        this.typeUtils = typeUtils;
        this.elementUtils = elementUtils;
    }

    @Override
    public Stream<CompilationError> visitDeclared(DeclaredType parameterType, VariableElement initialElement) {
        return Stream.concat(
            validate(parameterType, initialElement),
            parameterType.getTypeArguments().stream().flatMap(type -> visit(type, initialElement))
        );
    }

    @Override
    public Stream<CompilationError> visitPrimitive(PrimitiveType primitive, VariableElement initialElement) {
        return validate(primitive, initialElement);
    }

    @Override
    protected Stream<CompilationError> defaultAction(TypeMirror unknown, VariableElement initialElement) {
        return compilationError(initialElement);
    }

    private final Stream<TypeMirror> allowedTypes() {
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
            typeMirror(List.class)
        ).stream();
    }

    private Stream<CompilationError> validate(TypeMirror typeMirror, VariableElement initialElement) {
        if (!isValid(typeMirror)) {
            return compilationError(initialElement);
        }

        return Stream.empty();
    }

    private boolean isValid(TypeMirror typeMirror) {
        return allowedTypes().anyMatch(type -> {
            return typeUtils.isSameType(
                    typeUtils.erasure(type),
                    typeUtils.erasure(typeMirror)
            );
        }) && validAsMapType(typeMirror);
    }

    private boolean validAsMapType(TypeMirror typeMirror) {
        TypeElement mapElement = elementUtils.getTypeElement(Map.class.getCanonicalName());
        if (!typeUtils.isSameType(typeUtils.erasure(mapElement.asType()), typeUtils.erasure(typeMirror))) {
            return true; // check does not apply
        }

        TypeMirror validMapType = typeUtils.getDeclaredType(
            mapElement,
            elementUtils.getTypeElement(String.class.getCanonicalName()).asType(),
            elementUtils.getTypeElement(Object.class.getCanonicalName()).asType()
        );

        return typeUtils.isSameType(validMapType, typeMirror);
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

    private PrimitiveType primitive(TypeKind kind) {
        return typeUtils.getPrimitiveType(kind);
    }

    private TypeMirror boxed(PrimitiveType bool) {
        return typeUtils.boxedClass(bool).asType();
    }

    private TypeMirror typeMirror(Class<?> type) {
        return elementUtils.getTypeElement(type.getName()).asType();
    }
}
