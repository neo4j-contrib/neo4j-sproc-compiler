package net.biville.florent.sproccompiler.validators;

import java.util.Objects;

class ProcedureSourceLocation {

    private final CharSequence packageName;
    private final CharSequence methodName;

    public ProcedureSourceLocation(CharSequence packageName,
                                   CharSequence methodName) {

        this.packageName = packageName;
        this.methodName = methodName;
    }

    public CharSequence packageName() {
        return packageName;
    }

    public CharSequence methodName() {
        return methodName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageName, methodName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ProcedureSourceLocation other = (ProcedureSourceLocation) obj;
        return Objects.equals(this.packageName, other.packageName)
                && Objects.equals(this.methodName, other.methodName);
    }
}
