/*
 * Copyright 2016-2018 the original author or authors.
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

import net.biville.florent.sproccompiler.compilerutils.TypeMirrorUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor8;
import javax.lang.model.util.Types;

/**
 * This predicate makes sure that a given declared type (record field type,
 * procedure parameter type...) is supported by Neo4j stored procedures.
 */
public class AllowedTypesValidator implements Predicate<TypeMirror>
{

    private final TypeMirrorUtils typeMirrors;
    private final Collection<TypeMirror> whitelistedTypes;
    private final Types typeUtils;

    public AllowedTypesValidator( TypeMirrorUtils typeMirrors, Types typeUtils )
    {

        this.typeMirrors = typeMirrors;
        this.whitelistedTypes = typeMirrors.procedureAllowedTypes();
        this.typeUtils = typeUtils;
    }

    @Override
    public boolean test( TypeMirror typeMirror )
    {
        TypeMirror erasedActualType = typeUtils.erasure( typeMirror );

        return isValidErasedType( erasedActualType ) &&
                (!isSameErasedType( List.class, typeMirror ) || isValidListType( typeMirror )) &&
                (!isSameErasedType( Map.class, typeMirror ) || isValidMapType( typeMirror ));
    }

    private boolean isValidErasedType( TypeMirror actualType )
    {
        return whitelistedTypes.stream().anyMatch( type ->
        {
            TypeMirror erasedAllowedType = typeUtils.erasure( type );

            TypeMirror map = typeUtils.erasure( typeMirrors.typeMirror( Map.class ) );
            TypeMirror list = typeUtils.erasure( typeMirrors.typeMirror( List.class ) );
            if ( typeUtils.isSameType( erasedAllowedType, map ) || typeUtils.isSameType( erasedAllowedType, list ) )
            {
                return typeUtils.isSubtype( actualType, erasedAllowedType );
            }

            return typeUtils.isSameType( actualType, erasedAllowedType );
        } );
    }

    /**
     * Recursively visits List type arguments
     *
     * @param typeMirror the List type mirror
     * @return true if the declaration is valid, false otherwise
     */
    private boolean isValidListType( TypeMirror typeMirror )
    {
        return new SimpleTypeVisitor8<Boolean,Void>()
        {
            @Override
            public Boolean visitDeclared( DeclaredType list, Void aVoid )
            {
                List<? extends TypeMirror> typeArguments = list.getTypeArguments();
                if ( typeArguments.size() != 1 )
                {
                    return false;
                }
                return test( typeArguments.get( 0 ) );
            }
        }.visit( typeMirror );
    }

    /**
     * Recursively visits Map type arguments
     * Map key type argument must be a String as of Neo4j stored procedure specification
     * Map value type argument is recursively visited
     *
     * @param typeMirror Map type mirror
     * @return true if the declaration is valid, false otherwise
     */
    private boolean isValidMapType( TypeMirror typeMirror )
    {
        return new SimpleTypeVisitor8<Boolean,Void>()
        {
            @Override
            public Boolean visitDeclared( DeclaredType map, Void ignored )
            {
                List<? extends TypeMirror> typeArguments = map.getTypeArguments();
                if ( typeArguments.size() != 2 )
                {
                    return false;
                }

                TypeMirror key = typeArguments.get( 0 );
                if ( !typeUtils.isSameType( key, typeMirrors.typeMirror( String.class ) ) )
                {
                    return false;
                }
                return test( typeArguments.get( 1 ) );
            }
        }.visit( typeMirror );
    }

    private boolean isSameErasedType( Class<?> type, TypeMirror typeMirror )
    {
        return typeUtils
                .isSameType( typeUtils.erasure( typeMirrors.typeMirror( type ) ), typeUtils.erasure( typeMirror ) );
    }

}
