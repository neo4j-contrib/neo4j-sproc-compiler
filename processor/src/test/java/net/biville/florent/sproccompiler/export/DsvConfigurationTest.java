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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DsvConfigurationTest
{

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void parses_path_option() throws IOException
    {
        File folder = this.folder.newFolder();
        Map<String,String> options = new HashMap<>( 1 );
        options.put( "GeneratedDocumentationPath", folder.getAbsolutePath() );

        DsvConfiguration configuration = new DsvConfiguration( options );

        assertThat( configuration.getRootPath() ).isPresent().contains( folder.toPath() );

    }

    @Test
    public void parses_absent_path_option() throws IOException
    {
        DsvConfiguration configuration = new DsvConfiguration( new HashMap<>( 0 ) );

        assertThat( configuration.getRootPath() ).isEmpty();

    }

    @Test
    public void parses_invalid_path_option() throws IOException
    {
        Map<String,String> options = new HashMap<>( 1 );
        options.put( "GeneratedDocumentationPath", "/nope/no/way/" );

        DsvConfiguration configuration = new DsvConfiguration( options );

        assertThat( configuration.getRootPath() ).isEmpty();

    }

    @Test
    public void parses_field_delimiter()
    {
        Map<String,String> options = new HashMap<>( 1 );
        options.put( "Documentation.FieldDelimiter", "$$" );

        DsvConfiguration configuration = new DsvConfiguration( options );

        assertThat( configuration.getFieldDelimiter() ).isEqualTo( "$$" );

    }

    @Test
    public void parses_raw_exported_headers()
    {
        Map<String,String> options = new HashMap<>( 1 );
        options.put( "Documentation.ExportedHeaders", "foo,bar" );

        DsvConfiguration configuration = new DsvConfiguration( options );

        assertThat( configuration.getRawHeaders() ).isEqualTo( "foo,bar" );

    }

    @Test
    public void parses_default_raw_exported_headers()
    {
        DsvConfiguration configuration = new DsvConfiguration( new HashMap<>( 0 ) );

        assertThat( configuration.getRawHeaders() ).isEqualTo( "*" );

    }

    @Test
    public void parses_grouping_strategy()
    {
        Map<String,String> options = new HashMap<>( 1 );
        options.put( "Documentation.ExportGrouping", "CLASS,single,PACKage" );

        DsvConfiguration configuration = new DsvConfiguration( options );

        assertThat( configuration.getGroupingStrategies() )
                .containsOnlyOnce( DsvGroupingStrategy.CLASS, DsvGroupingStrategy.SINGLE, DsvGroupingStrategy.PACKAGE );

    }

    @Test
    public void parses_default_grouping_strategy()
    {
        DsvConfiguration configuration = new DsvConfiguration( new HashMap<>( 0 ) );

        assertThat( configuration.getGroupingStrategies() ).containsExactly( DsvGroupingStrategy.SINGLE );

    }

    @Test
    public void fails_parsing_invalid_grouping_strategy()
    {
        thrown.expect( IllegalArgumentException.class );
        thrown.expectMessage( "No enum constant net.biville.florent.sproccompiler.export.DsvGroupingStrategy.NOPE" );
        Map<String,String> options = new HashMap<>( 1 );
        options.put( "Documentation.ExportGrouping", "nope" );

        new DsvConfiguration( options );
    }

    @Test
    public void parses_split_strategy()
    {
        Map<String,String> options = new HashMap<>( 1 );
        options.put( "Documentation.ExportSplit", "kInD" );

        DsvConfiguration configuration = new DsvConfiguration( options );

        assertThat( configuration.getSplitStrategy() ).isEqualTo( DsvSplitStrategy.KIND );

    }

    @Test
    public void parses_default_split_strategy()
    {
        DsvConfiguration configuration = new DsvConfiguration( new HashMap<>( 0 ) );

        assertThat( configuration.getSplitStrategy() ).isEqualTo( DsvSplitStrategy.NONE );

    }

    @Test
    public void fails_parsing_invalid_split_strategy()
    {
        thrown.expect( IllegalArgumentException.class );
        thrown.expectMessage( "No enum constant net.biville.florent.sproccompiler.export.DsvSplitStrategy.NOPE" );
        Map<String,String> options = new HashMap<>( 1 );
        options.put( "Documentation.ExportSplit", "nope" );

        new DsvConfiguration( options );
    }
}
