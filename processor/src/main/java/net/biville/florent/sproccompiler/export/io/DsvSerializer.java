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
package net.biville.florent.sproccompiler.export.io;

import net.biville.florent.sproccompiler.export.DsvConfiguration;
import net.biville.florent.sproccompiler.export.DsvGroupingStrategy;
import net.biville.florent.sproccompiler.export.DsvProcessor;
import net.biville.florent.sproccompiler.export.DsvSplitStrategy;
import net.biville.florent.sproccompiler.export.Either;
import net.biville.florent.sproccompiler.export.MethodPartition;
import net.biville.florent.sproccompiler.export.elements.ConstantQualifiedNameable;
import net.biville.florent.sproccompiler.export.messages.DsvExportError;
import net.biville.florent.sproccompiler.messages.MessagePrinter;

import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DsvSerializer
{
    private final Elements elements;
    private final ElementVisitor<TypeElement,Void> enclosingTypeVisitor;
    private final MessagePrinter messagePrinter;
    private final DsvFieldExporter fieldExporter;

    public DsvSerializer( Elements elements, ElementVisitor<TypeElement,Void> enclosingTypeVisitor,
            MessagePrinter messagePrinter )
    {

        this.elements = elements;
        this.enclosingTypeVisitor = enclosingTypeVisitor;
        this.messagePrinter = messagePrinter;
        this.fieldExporter = new DsvFieldExporter( elements );
    }

    /**
     * Serializes processed extensions according to the input configuration.
     * The actual write operations are handled by {@link DsvFileWriter}.
     * <p>
     * The main added value of this indirection is to properly reorganize the data
     * so it complies to the user configuration, may it be in terms of grouping strategies
     * (see {@link DsvGroupingStrategy}), splitting strategy (see {@link DsvSplitStrategy}),
     * custom field delimiter and/or exported header selection.
     * <p>
     * All of these combinable settings are specified by {@link DsvConfiguration}, based on
     * the options passed to the annotation processor {@link DsvProcessor}.
     *
     * @param root root folder that is going to contain the generated files
     * @param configuration file grouping/item splitting options, exported headers
     * @param procedures processed user-defined procedures
     * @param functions processed user-defined functions
     */
    public void serialize( Path root, DsvConfiguration configuration, Collection<ExecutableElement> procedures,
            Collection<ExecutableElement> functions )
    {
        DsvSplitStrategy splitStrategy = configuration.getSplitStrategy();
        configuration.getGroupingStrategies().forEach( grouping ->
        {
            Collection<MethodPartition> partitions = splitStrategy.partition( procedures, functions );
            Function<ExecutableElement,QualifiedNameable> keyFunction = groupByFileBasename( grouping );
            serialize( root, configuration, partitions, keyFunction );
        } );
    }

    private void serialize( Path root, DsvConfiguration configuration, Collection<MethodPartition> partitions,
            Function<ExecutableElement,QualifiedNameable> keyFunction )
    {
        partitions.forEach( partition ->
        {
            String fileSuffix = partition.getFileSuffix();
            partition.getMethods().stream().collect( Collectors.groupingBy( keyFunction ) ).entrySet().forEach( kv ->
            {
                Either<DsvExportError,List<String>> headerParsingResult =
                        fieldExporter.exportHeaders( configuration.getFieldDelimiter(), configuration.getRawHeaders() );

                headerParsingResult.consume( messagePrinter::print, ( headers ) ->
                {
                    File file = new File( root.toFile(),
                            String.format( "%s%s.csv", kv.getKey().getQualifiedName(), fileSuffix ) );

                    serializeWithHeaders( file, configuration, kv.getValue(), headers );
                } );
            } );
        } );
    }

    private void serializeWithHeaders( File file, DsvConfiguration configuration, Collection<ExecutableElement> methods,
            Collection<String> headers )
    {
        try (Writer resource = new OutputStreamWriter( new FileOutputStream( file ), StandardCharsets.UTF_8 );
             DsvFileWriter writer = new DsvFileWriter( headers, resource, configuration.getFieldDelimiter(), configuration.isFirstFieldDelimited(), configuration.areFieldsQuoted() ) )
        {
            writer.write( methods.stream(), ( method ) -> fieldExporter.exportFields( method, headers ),
                    messagePrinter::print );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e.getMessage(), e );
        }
    }

    private Function<ExecutableElement,QualifiedNameable> groupByFileBasename( DsvGroupingStrategy grouping )
    {
        switch ( grouping )
        {

        case SINGLE:
            return ( ignored ) -> new ConstantQualifiedNameable( "documentation" );
        case PACKAGE:
            return elements::getPackageOf;
        case CLASS:
            return method -> enclosingTypeVisitor.visit( method.getEnclosingElement() );
        }
        throw new IllegalArgumentException( "Unknown grouping strategy: " + grouping );
    }

}
