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
package net.biville.florent.sproccompiler;

import com.google.testing.compile.CompilationRule;
import net.biville.florent.sproccompiler.testutils.JavaFileObjectUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.annotation.processing.Processor;
import javax.tools.JavaFileObject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.nio.file.Files.readAllLines;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class CsvProcessorTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    @Rule
    public CompilationRule compilation = new CompilationRule();
    private Processor processor = new CsvProcessor();
    private File folder;


    @Before
    public void prepare() throws IOException {
        folder = temporaryFolder.newFolder();
    }

    @Test
    public void dumps_procedure_definition_to_csv() throws IOException {
        Iterable<JavaFileObject> sources = asList(
                JavaFileObjectUtils.INSTANCE.procedureSource("valid/SimpleProcedures.java"),
                JavaFileObjectUtils.INSTANCE.procedureSource("valid/SimpleUserFunctions.java"));

        assert_().about( javaSources() ).that(sources)
                .withCompilerOptions("-AGeneratedDocumentationPath=" + folder.getAbsolutePath())
                .processedWith(processor)
                .compilesWithoutError();

        String namespace = "net.biville.florent.sproccompiler.procedures.valid.";
        String generatedCsv = readContents(Paths.get(folder.getAbsolutePath(), "valid.csv"));
        assertThat(generatedCsv).isEqualTo("" +
                "\"name\",\"description\",\"execution mode\",\"location\",\"deprecated by\"\n" +
                "\"" + namespace + "doSomething(int foo)\",\"\",\"PERFORMS_WRITE\",\"" + namespace + "SimpleProcedures\",\"doSomething2\"\n" +
                "\"" + namespace + "doSomething2(long bar)\",\"Much better than the former version\",\"SCHEMA\",\"" + namespace + "SimpleProcedures\",\"N/A\"\n" +
                "\"" + namespace + "sum(int a,int b)\",\"Performs super complex maths\",\"N/A\",\"" + namespace + "SimpleUserFunctions\",\"N/A\"");

    }

    private String readContents(Path path) throws IOException {
        return readAllLines(path)
                .stream()
                .collect(Collectors.joining("\n"));
    }
}
