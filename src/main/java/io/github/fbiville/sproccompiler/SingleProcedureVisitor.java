package io.github.fbiville.sproccompiler;

import org.neo4j.procedure.Name;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.stream.Stream;

class SingleProcedureVisitor extends SimpleElementVisitor8<Stream<CompilationError>, Void> {

    private final Types typeUtils;
    private final Elements elementUtils;
    private final TypeVisitor<Stream<CompilationError>, Void> recordVisitor;
    private final TypeVisitor<Stream<CompilationError>, VariableElement> parameterTypeVisitor;

    public SingleProcedureVisitor(Types typeUtils, Elements elementUtils) {
        this.typeUtils = typeUtils;
        this.elementUtils = elementUtils;
        this.recordVisitor = new RecordTypeVisitor(typeUtils, elementUtils);
        this.parameterTypeVisitor = new ParameterTypeVisitor(typeUtils, elementUtils);
    }

    /**
     * Validates method parameters and return type
     */
    @Override
    public Stream<CompilationError> visitExecutable(ExecutableElement executableElement, Void ignored) {
        return Stream.concat(
                validateParameters(executableElement.getParameters(), ignored),
                validateReturnType(executableElement)
        );
    }

    /**
     * Validates a method parameter
     */
    @Override
    public Stream<CompilationError> visitVariable(VariableElement parameter, Void ignored) {

        Name annotation = parameter.getAnnotation(Name.class);
        if (annotation == null) {
            return Stream.of(new ParameterMissingAnnotationError(
                    parameter,
                    annotationMirror(parameter.getAnnotationMirrors()),
                    "Missing @%s on parameter <%s>",
                    Name.class.getCanonicalName(),
                    nameOf(parameter)
            ));
        }

        return parameterTypeVisitor.visit(parameter.asType(), parameter);
    }

    private Stream<CompilationError> validateParameters(List<? extends VariableElement> parameters, Void ignored) {
        return parameters
                .stream()
                .flatMap(var -> visitVariable(var, ignored));
    }

    private Stream<CompilationError> validateReturnType(ExecutableElement method) {
        String streamClassName = Stream.class.getCanonicalName();

        TypeMirror expectedType = typeUtils.erasure(elementUtils.getTypeElement(streamClassName).asType());
        TypeMirror returnType = method.getReturnType();
        TypeMirror erasedReturnType = typeUtils.erasure(returnType);

        TypeMirror voidType = typeUtils.getNoType(TypeKind.VOID);
        if (typeUtils.isSameType(returnType, voidType)) {
            return Stream.empty();
        }

        if (!typeUtils.isSubtype(erasedReturnType, expectedType)) {
            return Stream.of(new ReturnTypeError(
                    method,
                    "Return type of %s#%s must be %s",
                    method.getEnclosingElement().getSimpleName(),
                    method.getSimpleName(),
                    streamClassName
            ));
        }

        return recordVisitor.visit(returnType);
    }

    private AnnotationMirror annotationMirror(List<? extends AnnotationMirror> mirrors) {
        return mirrors.stream()
                .filter(mirror -> {
                    DeclaredType actualType = mirror.getAnnotationType();
                    String expectedType = Name.class.getSimpleName();
                    return actualType.asElement().getSimpleName().contentEquals(expectedType);
                })
                .findFirst()
                .orElse(null);
    }

    private String nameOf(VariableElement parameter) {
        return parameter.getSimpleName().toString();
    }
}
