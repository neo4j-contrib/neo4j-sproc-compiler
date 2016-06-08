package io.github.fbiville.sproccompiler;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

class AllowedTypesValidator implements Predicate<TypeMirror> {

    private final TypeMirrors typeMirrors;
    private Collection<TypeMirror> whitelistedTypes;
    private final Types typeUtils;
    private final Elements elementUtils;

    public AllowedTypesValidator(Collection<TypeMirror> whitelistedTypes,
                                 Types typeUtils,
                                 Elements elementUtils) {

        this.whitelistedTypes = whitelistedTypes;
        this.typeUtils = typeUtils;
        this.elementUtils = elementUtils;
        this.typeMirrors = new TypeMirrors(typeUtils, elementUtils);
    }

    @Override
    public boolean test(TypeMirror typeMirror) {
        return allowedTypes().anyMatch(type -> {
            TypeMirror erasedAllowedType = typeUtils.erasure(type);
            TypeMirror erasedActualType = typeUtils.erasure(typeMirror);

            TypeMirror map = typeUtils.erasure(typeMirrors.typeMirror(Map.class));
            TypeMirror list = typeUtils.erasure(typeMirrors.typeMirror(List.class));
            if (typeUtils.isSameType(erasedAllowedType, map) || typeUtils.isSameType(erasedAllowedType, list)) {
                return typeUtils.isSubtype(erasedActualType, erasedAllowedType);
            }

            return typeUtils.isSameType(erasedActualType, erasedAllowedType);
        }) && validAsMapType(typeMirror);
    }

    private boolean validAsMapType(TypeMirror typeMirror) {
        TypeElement mapElement = elementUtils.getTypeElement(Map.class.getCanonicalName());
        if (!typeUtils.isSameType(typeUtils.erasure(mapElement.asType()), typeUtils.erasure(typeMirror))) {
            return true; // check does not apply
        }

        TypeMirror validMapType = typeUtils.getDeclaredType(
                mapElement,
                elementUtils.getTypeElement(String.class.getCanonicalName()).asType(),
                elementUtils.getTypeElement(Object.class.getCanonicalName()).asType()
        );

        return typeUtils.isSameType(validMapType, typeMirror);
    }

    private Stream<TypeMirror> allowedTypes() {
        return whitelistedTypes.stream();
    }
}
