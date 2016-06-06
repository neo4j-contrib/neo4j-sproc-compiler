package io.github.fbiville.sproccompiler;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

class ParameterMissingAnnotationError implements CompilationError {
    private final Element element;
    private final AnnotationMirror mirror;
    private final String errorMessage;

    public ParameterMissingAnnotationError(Element element, AnnotationMirror mirror, String errorMessage, String... args) {
        this.element = element;
        this.mirror = mirror;
        this.errorMessage = String.format(errorMessage, args);
    }

    @Override
    public Element getElement() {
        return element;
    }

    @Override
    public AnnotationMirror getMirror() {
        return mirror;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}
