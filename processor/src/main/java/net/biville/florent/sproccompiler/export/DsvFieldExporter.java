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
package net.biville.florent.sproccompiler.export;

import net.biville.florent.sproccompiler.export.messages.DsvExportError;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.util.Elements;

import static java.util.stream.Collectors.toList;

class DsvFieldExporter
{

    private final DsvFieldSerializers serializers;

    public DsvFieldExporter( Elements elementUtils )
    {
        this.serializers = new DsvFieldSerializers( elementUtils );
    }

    public Either<DsvExportError, List<String>> exportHeaders( String delimiter, String headerInput )
    {
        String inputOption = headerInput.trim();

        List<String> allFields = serializers.getAllFields();
        if ( inputOption.equals( "*" ) )
        {
            return Either.right( allFields );
        }

        List<String> result = sanitize( delimiter, inputOption );
        return validateHeaders( delimiter, allFields, result );
    }

    public Stream<Either<DsvExportError, String>> exportFields( ExecutableElement method, Collection<String>
            exportedHeaders )
    {
        return exportedHeaders.stream().map( f -> serializers.serializeField( method, f ) );
    }

    private List<String> sanitize( String delimiter, String inputOption )
    {
        return Arrays.stream( inputOption.split( String.format( "\\Q%s\\E", delimiter ) ) ).map( String::trim )
                .distinct().collect( toList() );
    }

    private Either<DsvExportError, List<String>> validateHeaders( String delimiter, List<String> allFields,
            List<String> result )
    {
        List<String> faultyHeaders = validate( allFields, result );
        if ( faultyHeaders.isEmpty() )
        {
            return Either.right( result );
        }

        return Either.left( new DsvExportError( null,
                "%nExported comma-separated header contains invalid values: %s. %n" +
                        "\tDelimiter should be: %s %n" + "\tValid values are '*' or '%s'", faultyHeaders, delimiter,
                String.join( delimiter, allFields ) ) );

    }

    private List<String> validate( List<String> allFields, List<String> result )
    {
        return result.stream().filter( t -> !allFields.contains( t ) ).collect( toList() );
    }
}
