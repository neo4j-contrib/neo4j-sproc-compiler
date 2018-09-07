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
package net.biville.florent.sproccompiler.export.elements;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.type.TypeMirror;

public class ConstantQualifiedNameable implements QualifiedNameable
{

    private final String basename;

    public ConstantQualifiedNameable( String basename )
    {
        this.basename = basename;
    }

    @Override
    public Name getQualifiedName()
    {
        return new QualifiedName( basename );
    }

    @Override
    public TypeMirror asType()
    {
        return null;
    }

    @Override
    public ElementKind getKind()
    {
        return null;
    }

    @Override
    public Set<Modifier> getModifiers()
    {
        return null;
    }

    @Override
    public Name getSimpleName()
    {
        return null;
    }

    @Override
    public Element getEnclosingElement()
    {
        return null;
    }

    @Override
    public List<? extends Element> getEnclosedElements()
    {
        return null;
    }

    @Override
    public List<? extends AnnotationMirror> getAnnotationMirrors()
    {
        return null;
    }

    @Override
    public <A extends Annotation> A getAnnotation( Class<A> annotationType )
    {
        return null;
    }

    @Override
    public <A extends Annotation> A[] getAnnotationsByType( Class<A> annotationType )
    {
        return null;
    }

    @Override
    public <R, P> R accept( ElementVisitor<R,P> v, P p )
    {
        return null;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( basename );
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null || getClass() != obj.getClass() )
        {
            return false;
        }
        final ConstantQualifiedNameable other = (ConstantQualifiedNameable) obj;
        return Objects.equals( this.basename, other.basename );
    }
}
