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
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

class DsvConfiguration
{

    private static final String DOCUMENTATION_ROOT_PATH = "GeneratedDocumentationPath";
    private static final String DOCUMENTATION_FIELD_DELIMITER = "Documentation.FieldDelimiter";
    private static final String DOCUMENTATION_EXPORTED_HEADERS = "Documentation.ExportedHeaders";

    private final Optional<Path> rootPath;
    private final String fieldDelimiter;
    private final String rawHeaders;

    public DsvConfiguration( Map<String,String> actualOptions )
    {
        rootPath = Optional.ofNullable( actualOptions.getOrDefault( DOCUMENTATION_ROOT_PATH, null ) ).map( Paths::get );
        fieldDelimiter = actualOptions.getOrDefault( DOCUMENTATION_FIELD_DELIMITER, "," );
        rawHeaders = actualOptions.getOrDefault( DOCUMENTATION_EXPORTED_HEADERS, "*" );
    }

    public static Set<String> getSupportedOptions()
    {
        Set<String> options = new HashSet<>( 3 );
        options.add( DOCUMENTATION_ROOT_PATH );
        options.add( DOCUMENTATION_EXPORTED_HEADERS );
        options.add( DOCUMENTATION_FIELD_DELIMITER );
        return options;
    }

    public Optional<Path> getRootPath()
    {
        return rootPath;
    }

    public String getFieldDelimiter()
    {
        return fieldDelimiter;
    }

    public String getRawHeaders()
    {
        return rawHeaders;
    }
}
