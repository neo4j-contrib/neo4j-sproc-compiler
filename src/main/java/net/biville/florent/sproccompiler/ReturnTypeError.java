package net.biville.florent.sproccompiler;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

class ReturnTypeError implements CompilationError {

    private final Element element;
    private final String errorMessage;

    public ReturnTypeError(Element element, String errorMessage, CharSequence... args) {
        this.element = element;
        this.errorMessage = String.format(errorMessage, args);
    }

    @Override
    public Element getElement() {
        return element;
    }

    @Override
    public AnnotationMirror getMirror() {
        return null;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}
