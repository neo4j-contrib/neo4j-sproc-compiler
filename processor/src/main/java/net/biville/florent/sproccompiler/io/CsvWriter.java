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
package net.biville.florent.sproccompiler.io;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class CsvWriter implements AutoCloseable {

    private final Collection<String> header;
    private final Writer writer;
    private final String separator;

    public CsvWriter(Collection<String> header, Writer writer) {
        this.header = header;
        this.writer = writer;
        this.separator = ",";
    }

    public <T> void write(Stream<T> records, Function<T, Stream<String>> rowFunction) {
        List<String> rows =
                records
                        .map(record -> mapToRow(rowFunction, record))
                        .collect(Collectors.toList());

        writeRow(joinFields(header.stream()));
        rows.forEach(this::writeRow);
    }

    @Override
    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private <T> String mapToRow(Function<T, Stream<String>> rowFunction, T record) {
        Stream<String> fields = rowFunction.apply(record);
        return joinFields(fields);
    }

    private String joinFields(Stream<String> fields) {
        return fields
                .map(field -> "\"" + field.replace("\"", "\"\"") + "\"")
                .collect(joining(separator));
    }

    private void writeRow(String row) {
        try {
            writer.write(row + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
