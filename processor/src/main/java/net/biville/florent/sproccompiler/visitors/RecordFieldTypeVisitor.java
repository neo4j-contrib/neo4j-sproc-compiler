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
package net.biville.florent.sproccompiler.visitors;

import net.biville.florent.sproccompiler.compilerutils.TypeMirrorUtils;
import net.biville.florent.sproccompiler.validators.AllowedTypesValidator;

import java.util.function.Predicate;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor8;
import javax.lang.model.util.Types;

class RecordFieldTypeVisitor extends SimpleTypeVisitor8<Boolean,Void>
{

    private final Predicate<TypeMirror> allowedTypesValidator;

    public RecordFieldTypeVisitor( Types typeUtils, TypeMirrorUtils typeMirrors )
    {
        super( Boolean.FALSE );
        allowedTypesValidator = new AllowedTypesValidator( typeMirrors, typeUtils );
    }

    @Override
    public Boolean visitDeclared( DeclaredType declaredType, Void ignored )
    {
        return allowedTypesValidator.test( declaredType ) &&
                declaredType.getTypeArguments().stream().map( this::visit ).reduce( ( a, b ) -> a && b ).orElse( true );
    }

    @Override
    public Boolean visitPrimitive( PrimitiveType primitiveType, Void ignored )
    {
        return allowedTypesValidator.test( primitiveType );
    }
}
