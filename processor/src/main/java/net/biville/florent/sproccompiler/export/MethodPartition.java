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
package net.biville.florent.sproccompiler.export;

import java.util.Collection;
import java.util.Objects;
import javax.lang.model.element.ExecutableElement;

public class MethodPartition
{
    private final String suffix;
    private final Collection<ExecutableElement> methods;

    public MethodPartition( String suffix, Collection<ExecutableElement> methods )
    {
        this.suffix = suffix;
        this.methods = methods;
    }

    public String getFileSuffix()
    {
        return suffix;
    }

    public Collection<ExecutableElement> getMethods()
    {
        return methods;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( suffix, methods );
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
        final MethodPartition other = (MethodPartition) obj;
        return Objects.equals( this.suffix, other.suffix ) && Objects.equals( this.methods, other.methods );
    }
}
