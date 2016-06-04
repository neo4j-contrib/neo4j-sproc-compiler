package io.github.fbiville.sproccompiler;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor8;
import javax.lang.model.util.Types;
import java.util.Set;
import java.util.stream.Stream;

import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

public class RecordTypeVisitor extends SimpleTypeVisitor8<Stream<CompilationError>, Void> {

    private final Types typeUtils;

    public RecordTypeVisitor(Types typeUtils) {
        this.typeUtils = typeUtils;
    }

    @Override
    public Stream<CompilationError> visitDeclared(DeclaredType returnType, Void aVoid) {
        return returnType.getTypeArguments()
                .stream()
                .flatMap(this::validateRecordType);
    }

    private Stream<CompilationError> validateRecordType(TypeMirror recordType) {
        Element recordElement = typeUtils.asElement(recordType);
        return recordElement
                .getEnclosedElements()
                .stream()
                .filter(element -> element.getKind() == ElementKind.FIELD)
                .filter(element -> {
                    Set<Modifier> modifiers = element.getModifiers();
                    return !modifiers.contains(PUBLIC) && !modifiers.contains(STATIC);
                })
                .map(element -> new RecordTypeError(
                        element,
                        "Field %s#%s must be public",
                        recordElement.getSimpleName(),
                        element.getSimpleName()
                ));
    }

}
