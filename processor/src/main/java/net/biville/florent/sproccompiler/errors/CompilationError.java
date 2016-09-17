package net.biville.florent.sproccompiler.errors;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

public interface CompilationError {
    Element getElement();

    AnnotationMirror getMirror() ;

    String getErrorMessage();
}

