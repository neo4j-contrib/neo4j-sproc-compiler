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

import net.biville.florent.sproccompiler.export.io.DsvFieldExporter;
import net.biville.florent.sproccompiler.export.messages.DsvExportError;
import org.junit.Test;

import java.util.List;
import javax.lang.model.util.Elements;

import static net.biville.florent.sproccompiler.export.EitherAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class DsvFieldExporterTest
{

    private DsvFieldExporter parser = new DsvFieldExporter( mock( Elements.class ) );

    @Test
    public void parses_wildcard_as_all_header_values()
    {
        Either<DsvExportError,List<String>> headers = parser.exportHeaders( "$", "*" );

        assertThat( headers ).isRight().verifiesRight( ( right ) ->
        {
            assertThat( right )
                    .containsExactly( "type", "name", "description", "execution mode", "location", "deprecated by" );
        } );
    }

    @Test
    public void parses_exported_headers_in_order()
    {
        Either<DsvExportError,List<String>> headers = parser.exportHeaders( "$", "type$deprecated by" );

        assertThat( headers ).isRight().verifiesRight( ( right ) ->
        {
            assertThat( right ).containsExactly( "type", "deprecated by" );
        } );
    }

    @Test
    public void parses_exported_headers_in_order_after_trimming()
    {
        Either<DsvExportError,List<String>> headers = parser.exportHeaders( "$", "    description  $   location   " );

        assertThat( headers ).isRight().verifiesRight( ( right ) ->
        {
            assertThat( right ).containsExactly( "description", "location" );
        } );
    }

    @Test
    public void can_split_by_characters_reserved_for_regexes()
    {
        Either<DsvExportError,List<String>> headers = parser.exportHeaders( "[", "description[execution mode" );

        assertThat( headers ).isRight().verifiesRight( ( right ) ->
        {
            assertThat( right ).containsExactly( "description", "execution mode" );
        } );
    }

    @Test
    public void reports_invalid_fields()
    {
        Either<DsvExportError,List<String>> headers =
                parser.exportHeaders( "$", "    made-up-field  $   what-am-i-doing   " );

        assertThat( headers ).isLeft().verifiesLeft( ( left ) ->
        {
            assertThat( left ).isEqualTo( new DsvExportError( null, "%n" +
                    "Exported delimiter-separated header contains invalid values: [made-up-field, what-am-i-doing]. %n" +
                    "\tDelimiter should be: $ %n" +
                    "\tValid values are '*' or any subset of 'type$name$description$execution mode$location$deprecated by'" ) );
        } );
    }
}
