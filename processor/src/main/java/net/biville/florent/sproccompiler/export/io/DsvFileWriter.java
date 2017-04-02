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
package net.biville.florent.sproccompiler.export.io;

import net.biville.florent.sproccompiler.export.Either;
import net.biville.florent.sproccompiler.export.messages.DsvExportError;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class DsvFileWriter implements AutoCloseable
{

    private final Collection<String> header;
    private final Writer writer;
    private final String separator;
    private final boolean delimitFirstField;
    private final boolean quoteFields;

    public DsvFileWriter( Collection<String> header, Writer writer )
    {
        this( header, writer, "," , false, true);
    }

    public DsvFileWriter( Collection<String> header, Writer writer, String separator, boolean delimitFirstField, boolean quoteFields )
    {
        this.header = header;
        this.writer = writer;
        this.separator = separator;
        this.delimitFirstField = delimitFirstField;
        this.quoteFields = quoteFields;
    }

    public <T> void write( Stream<T> records, Function<T,Stream<Either<DsvExportError,String>>> rowFunction,
            Consumer<DsvExportError> onError )
    {

        writeRow( joinFields( header.stream() ) );
        records.forEach( record ->
        {
            Stream<Either<DsvExportError,String>> parsingResult = rowFunction.apply( record );
            Either.combine( parsingResult )
                    .consume( errors -> errors.forEach( onError ), result -> writeRow( joinFields( result ) ) );
        } );
    }

    @Override
    public void close()
    {
        try
        {
            writer.flush();
            writer.close();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e.getMessage(), e );
        }
    }

    private String joinFields( Stream<String> fields )
    {
        String result = fields.map(field -> {
            if (quoteFields) {
                return "\"" + field.replace("\"", "\"\"") + "\"";
            }
            return field;

        }).collect(joining(separator));
        if (delimitFirstField) {
            return separator + result;
        }
        return result;
    }

    private void writeRow( String row )
    {
        try
        {
            writer.write( row + "\n" );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e.getMessage(), e );
        }
    }
}
