package io.github.fbiville.sproccompiler;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

class AllowedTypesValidator implements Predicate<TypeMirror> {

    private Collection<TypeMirror> whitelistedTypes;
    private final Types typeUtils;
    private final Elements elementUtils;

    public AllowedTypesValidator(Collection<TypeMirror> whitelistedTypes,
                                 Types typeUtils,
                                 Elements elementUtils) {

        this.whitelistedTypes = whitelistedTypes;
        this.typeUtils = typeUtils;
        this.elementUtils = elementUtils;
    }

    @Override
    public boolean test(TypeMirror typeMirror) {
        return allowedTypes().anyMatch(type -> {
            return typeUtils.isSameType(
                    typeUtils.erasure(type),
                    typeUtils.erasure(typeMirror)
            );
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
