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
