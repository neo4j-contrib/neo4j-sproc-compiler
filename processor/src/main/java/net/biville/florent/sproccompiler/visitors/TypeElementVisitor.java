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

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.tools.Diagnostic;

public class TypeElementVisitor extends SimpleElementVisitor8<TypeElement,Void>
{
    private final Messager messager;

    public TypeElementVisitor( Messager messager )
    {
        this.messager = messager;
    }

    @Override
    public TypeElement visitType( TypeElement e, Void aVoid )
    {
        return e;
    }

    @Override
    public TypeElement visitUnknown( Element e, Void aVoid )
    {
        messager.printMessage( Diagnostic.Kind.ERROR, "Expected an enclosing class, got: " + e.toString(), e );
        return null;
    }
}
