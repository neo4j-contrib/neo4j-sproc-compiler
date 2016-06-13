package net.biville.florent.sproccompiler;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

public interface CompilationError {
    Element getElement();

    AnnotationMirror getMirror() ;

    String getErrorMessage();
}

