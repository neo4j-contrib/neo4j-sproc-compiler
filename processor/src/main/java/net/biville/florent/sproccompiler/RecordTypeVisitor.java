package net.biville.florent.sproccompiler;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleTypeVisitor8;
import javax.lang.model.util.Types;
import java.util.Set;
import java.util.stream.Stream;

import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.util.ElementFilter.fieldsIn;

class RecordTypeVisitor extends SimpleTypeVisitor8<Stream<CompilationError>, Void> {

    private final Types typeUtils;
    private final TypeVisitor<Boolean, Void> fieldTypeVisitor;

    public RecordTypeVisitor(Types typeUtils, Elements elementUtils) {
        this.typeUtils = typeUtils;
        fieldTypeVisitor = new RecordFieldTypeVisitor(typeUtils, elementUtils);
    }

    @Override
    public Stream<CompilationError> visitDeclared(DeclaredType returnType, Void ignored) {
        return returnType.getTypeArguments()
                .stream()
                .flatMap(this::validateRecord);
    }

    private Stream<CompilationError> validateRecord(TypeMirror recordType) {
        Element recordElement = typeUtils.asElement(recordType);
        return Stream.concat(
                validateFieldModifiers(recordElement),
                validateFieldType(recordElement)
        );
    }

    private Stream<CompilationError> validateFieldModifiers(Element recordElement) {
        return fieldsIn(recordElement.getEnclosedElements())
                .stream()
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

    private Stream<CompilationError> validateFieldType(Element recordElement) {
        return fieldsIn(recordElement.getEnclosedElements())
                .stream()
                .filter(element -> !element.getModifiers().contains(STATIC))
                .filter(element -> !fieldTypeVisitor.visit(element.asType()))
                .map(element -> new RecordTypeError(
                        element,
                        "Type of field %s#%s is not supported",
                        recordElement.getSimpleName(),
                        element.getSimpleName()
                ));
    }

}
