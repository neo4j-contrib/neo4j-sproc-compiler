package io.github.fbiville.sproccompiler;

import org.neo4j.procedure.Name;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.SimpleElementVisitor8;
import java.util.List;
import java.util.stream.Stream;

public class ParameterVisitor extends SimpleElementVisitor8<Stream<CompilationError>, Void> {

    @Override
    public Stream<CompilationError> visitExecutable(ExecutableElement executableElement, Void ignored) {
        return executableElement.getParameters()
                .stream()
                .flatMap(var -> visitVariable(var, ignored));
    }

    @Override
    public Stream<CompilationError> visitVariable(VariableElement parameter, Void ignored) {

        Name annotation = parameter.getAnnotation(Name.class);
        if (annotation == null) {
            return Stream.of(new CompilationError(
                    parameter,
                    annotationMirror(parameter.getAnnotationMirrors()),
                    "Missing @%s on parameter <%s>",
                    Name.class.getCanonicalName(),
                    nameOf(parameter)
            ));
        }
        return Stream.empty();
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
