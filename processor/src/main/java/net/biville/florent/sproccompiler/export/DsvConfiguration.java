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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Boolean.parseBoolean;

public class DsvConfiguration
{

    public static final String DOCUMENTATION_ROOT_PATH = "GeneratedDocumentationPath";
    private static final String DOCUMENTATION_FIELD_DELIMITER = "Documentation.FieldDelimiter";
    private static final String DOCUMENTATION_DELIMITED_FIRST_FIELD = "Documentation.DelimitedFirstField";
    private static final String DOCUMENTATION_QUOTED_FIELDS = "Documentation.QuotedFields";
    private static final String DOCUMENTATION_EXPORTED_HEADERS = "Documentation.ExportedHeaders";
    private static final String DOCUMENTATION_EXPORT_GROUPING = "Documentation.ExportGrouping";
    private static final String DOCUMENTATION_EXPORT_SPLIT = "Documentation.ExportSplit";

    private final Optional<Path> rootPath;
    private final String fieldDelimiter;
    private final String rawHeaders;
    private final EnumSet<DsvGroupingStrategy> groupingStrategy;
    private final DsvSplitStrategy splitStrategy;
    private final boolean delimitedFirstField;
    private final boolean quotedFields;

    public DsvConfiguration( Map<String,String> actualOptions )
    {
        rootPath = Optional.ofNullable( actualOptions.getOrDefault( DOCUMENTATION_ROOT_PATH, null ) ).map( Paths::get );
        fieldDelimiter = actualOptions.getOrDefault( DOCUMENTATION_FIELD_DELIMITER, "," );
        delimitedFirstField = parseBoolean(actualOptions.getOrDefault( DOCUMENTATION_DELIMITED_FIRST_FIELD, "false" ));
        quotedFields = parseBoolean(actualOptions.getOrDefault( DOCUMENTATION_QUOTED_FIELDS, "true" ));
        rawHeaders = actualOptions.getOrDefault( DOCUMENTATION_EXPORTED_HEADERS, "*" );
        groupingStrategy = parseGroupingStrategy(
                actualOptions.getOrDefault( DOCUMENTATION_EXPORT_GROUPING, "SINGLE" ).toUpperCase( Locale.ENGLISH ),
                "," );
        splitStrategy = DsvSplitStrategy.valueOf(
                actualOptions.getOrDefault( DOCUMENTATION_EXPORT_SPLIT, "NONE" ).toUpperCase( Locale.ENGLISH ) );
    }

    public static Set<String> getSupportedOptions()
    {
        Set<String> options = new HashSet<>( 7 );
        options.add( DOCUMENTATION_ROOT_PATH );
        options.add( DOCUMENTATION_EXPORTED_HEADERS );
        options.add( DOCUMENTATION_FIELD_DELIMITER );
        options.add( DOCUMENTATION_QUOTED_FIELDS );
        options.add( DOCUMENTATION_DELIMITED_FIRST_FIELD );
        options.add( DOCUMENTATION_EXPORT_GROUPING );
        options.add( DOCUMENTATION_EXPORT_SPLIT );
        return options;
    }

    private static EnumSet<DsvGroupingStrategy> parseGroupingStrategy( String rawOption, String delimiter )
    {
        return EnumSet.copyOf(
                Arrays.stream( rawOption.split( delimiter ) ).map( String::trim ).map( DsvGroupingStrategy::valueOf )
                        .collect( Collectors.toList() ) );
    }

    public Optional<Path> getRootPath()
    {
        return rootPath;
    }

    public String getFieldDelimiter()
    {
        return fieldDelimiter;
    }

    public boolean isFirstFieldDelimited() {
        return delimitedFirstField;
    }

    public boolean areFieldsQuoted() {
        return quotedFields;
    }

    public String getRawHeaders()
    {
        return rawHeaders;
    }

    public EnumSet<DsvGroupingStrategy> getGroupingStrategies()
    {
        return groupingStrategy;
    }

    public DsvSplitStrategy getSplitStrategy()
    {
        return splitStrategy;
    }
}
