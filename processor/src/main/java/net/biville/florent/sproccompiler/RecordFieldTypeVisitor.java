package net.biville.florent.sproccompiler;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleTypeVisitor8;
import javax.lang.model.util.Types;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static java.util.Arrays.asList;

class RecordFieldTypeVisitor extends SimpleTypeVisitor8<Boolean, Void> {

    private final TypeMirrors typeMirrors;
    private final Predicate<TypeMirror> allowedTypesValidator;

    public RecordFieldTypeVisitor(Types typeUtils, Elements elementUtils) {
        typeMirrors = new TypeMirrors(typeUtils, elementUtils);
        allowedTypesValidator = new AllowedTypesValidator(
            typeMirrors.procedureAllowedTypes(),
            typeUtils,
            elementUtils
        );
    }

    @Override
    public Boolean visitDeclared(DeclaredType declaredType, Void ignored) {
        return allowedTypesValidator.test(declaredType)
                && declaredType.getTypeArguments().stream()
                    .map(this::visit)
                    .reduce((a,b) -> a && b)
                    .orElse(true);
    }

    @Override
    public Boolean visitPrimitive(PrimitiveType primitiveType, Void ignored) {
        return allowedTypesValidator.test(primitiveType);
    }
}
