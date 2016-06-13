package net.biville.florent.sproccompiler;

import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

class TypeMirrors {

    private Types typeUtils;
    private Elements elementUtils;

    public TypeMirrors(Types typeUtils, Elements elementUtils) {
        this.typeUtils = typeUtils;
        this.elementUtils = elementUtils;
    }

    public PrimitiveType primitive(TypeKind kind) {
        return typeUtils.getPrimitiveType(kind);
    }

    public TypeMirror boxed(PrimitiveType bool) {
        return typeUtils.boxedClass(bool).asType();
    }

    public TypeMirror typeMirror(Class<?> type) {
        return elementUtils.getTypeElement(type.getName()).asType();
    }
}
