package io.github.fbiville.sproccompiler;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

public interface CompilationError {
    Element getElement();

    AnnotationMirror getMirror() ;

    String getErrorMessage();
}

