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

import javax.lang.model.element.Name;

class QualifiedName implements Name
{
    private final CharSequence name;

    public QualifiedName( CharSequence name )
    {
        this.name = name;
    }

    @Override
    public boolean contentEquals( CharSequence cs )
    {
        return name.equals( cs );
    }

    @Override
    public int length()
    {
        return name.length();
    }

    @Override
    public char charAt( int index )
    {
        return name.charAt( index );
    }

    @Override
    public CharSequence subSequence( int start, int end )
    {
        return name.subSequence( start, end );
    }

    @Override
    public String toString()
    {
        return name.toString();
    }
}
