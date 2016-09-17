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
package net.biville.florent.sproccompiler.validators;

import javax.lang.model.element.Element;
import java.util.Objects;

class VisitedProcedureMapEntry {

    private final Element element;
    private final ProcedureSourceLocation procedureSourceLocation;

    public VisitedProcedureMapEntry(Element element) {
        this.element = element;
        this.procedureSourceLocation = new ProcedureSourceLocation(
                element.getEnclosingElement().getEnclosingElement().getSimpleName(),
                element.getSimpleName()
        );
    }

    public ProcedureSourceLocation key() {
        return procedureSourceLocation;
    }

    public Element value() {
        return element;
    }

    @Override
    public int hashCode() {
        return Objects.hash(element);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final VisitedProcedureMapEntry other = (VisitedProcedureMapEntry) obj;
        return Objects.equals(this.element, other.element);
    }
}
