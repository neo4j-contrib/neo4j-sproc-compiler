package io.github.fbiville.sproccompiler;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

public class CompilationError {


    private final Element element;
    private final AnnotationMirror mirror;
    private final String errorMessage;

    public CompilationError(Element element, AnnotationMirror mirror, String errorMessage, String... args) {
        this.element = element;
        this.mirror = mirror;
        this.errorMessage = String.format(errorMessage, args);
    }

    public Element getElement() {
        return element;
    }

    public AnnotationMirror getMirror() {
        return mirror;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
