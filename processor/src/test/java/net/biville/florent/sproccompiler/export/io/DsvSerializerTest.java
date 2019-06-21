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

import com.google.testing.compile.CompilationRule;
import net.biville.florent.sproccompiler.export.DsvConfiguration;
import net.biville.florent.sproccompiler.messages.MessagePrinter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.neo4j.procedure.Procedure;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class DsvSerializerTest {

    @Rule
    public CompilationRule compilation = new CompilationRule();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private Elements elements;
    private Types types;
    private DsvSerializer dsvSerializer;

    @Before
    public void prepare() {
        elements = compilation.getElements();
        types = compilation.getTypes();
        dsvSerializer = new DsvSerializer(
                elements,
                mock(ElementVisitor.class),
                mock(MessagePrinter.class)
        );
    }

    @Test
    public void does_not_corrupt_unicode_characters() throws IOException {
        File root = folder.newFolder();
        ExecutableElement executableElement = singleExecutableElement(SimpleProcedure.class);

        dsvSerializer.serialize(root.toPath(), new DsvConfiguration(new HashMap<>()), singletonList(executableElement), emptyList());

        assertThat(singleDocumentationFile(root, "documentation.csv"))
                .usingCharset(StandardCharsets.UTF_8)
                .hasContent("\"type\",\"qualified name\",\"signature\",\"description\",\"execution mode\",\"location\",\"deprecated by\"\n" +
                        "\"procedure\",\"net.biville.florent.sproccompiler.export.io.doSomethingŞapşal\",\"java.lang.String doSomethingŞapşal()\",\"\",\"DEFAULT\",\"net.biville.florent.sproccompiler.export.io.SimpleProcedure\",\"\"");
    }

    private ExecutableElement singleExecutableElement(Class<SimpleProcedure> type) {
        TypeElement procedureClass = elements.getTypeElement(type.getName());
        List<? extends Element> members = elements.getAllMembers(procedureClass).stream()
                .filter(e -> e instanceof ExecutableElement && e.getSimpleName().contentEquals("doSomethingŞapşal"))
                .collect(toList());
        assertThat(members)
                .overridingErrorMessage("Expected only 1 method on %s, found %d", type.getName(), members.size())
                .hasSize(1);

        Element method = members.iterator().next();
        assertThat(method).overridingErrorMessage("Method should be an ExecutableElement, found %s", method.getClass()).isInstanceOf(ExecutableElement.class);
        return (ExecutableElement) method;
    }

    private File singleDocumentationFile(File root, String name) {
        File[] files = root.listFiles(pathname -> pathname.getName().equals(name));
        assertThat(files).hasSize(1);
        return files[0];
    }
}

class SimpleProcedure {

    @Procedure
    public String doSomethingŞapşal() {
        return "hello";
    }
}