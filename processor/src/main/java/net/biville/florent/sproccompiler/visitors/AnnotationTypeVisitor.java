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
package net.biville.florent.sproccompiler.visitors;

import java.lang.annotation.Annotation;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.SimpleElementVisitor8;

public class AnnotationTypeVisitor extends SimpleElementVisitor8<Boolean,Void>
{

    private final Class<? extends Annotation> annotationType;

    public AnnotationTypeVisitor( Class<? extends Annotation> annotationType )
    {
        this.annotationType = annotationType;
    }

    @Override
    public Boolean visitType( TypeElement element, Void aVoid )
    {
        return element.getQualifiedName().contentEquals( annotationType.getName() );
    }
}
