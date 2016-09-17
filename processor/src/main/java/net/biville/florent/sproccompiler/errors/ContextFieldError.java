/*
 * Copyright 2016-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.biville.florent.sproccompiler.errors;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

public class ContextFieldError implements CompilationError {

    private final Element field;
    private final String errorMessage;

    public ContextFieldError(Element field, String errorMessage, CharSequence... args) {

        this.field = field;
        this.errorMessage = String.format(errorMessage, args);
    }

    @Override
    public Element getElement() {
        return field;
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
