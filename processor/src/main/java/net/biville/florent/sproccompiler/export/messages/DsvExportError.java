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
package net.biville.florent.sproccompiler.export.messages;

import net.biville.florent.sproccompiler.messages.CompilationMessage;

import javax.lang.model.element.Element;

public class DsvExportError implements CompilationMessage
{
    private final Element element;
    private final String contents;

    public DsvExportError( Element element, String message, Object... args )
    {

        this.element = element;
        this.contents = String.format( message, args );
    }

    @Override
    public Element getElement()
    {
        return element;
    }

    @Override
    public String getContents()
    {
        return contents;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        DsvExportError that = (DsvExportError) o;

        if ( element != null ? !element.equals( that.element ) : that.element != null )
        {
            return false;
        }
        return contents != null ? contents.equals( that.contents ) : that.contents == null;
    }

    @Override
    public int hashCode()
    {
        int result = element != null ? element.hashCode() : 0;
        result = 31 * result + (contents != null ? contents.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DsvExportError{" +
                "contents='" + contents + '\'' +
                '}';
    }
}
