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
package net.biville.florent.sproccompiler.visitors;

import com.google.testing.compile.CompilationRule;
import net.biville.florent.sproccompiler.compilerutils.TypeMirrorUtils;
import net.biville.florent.sproccompiler.messages.CompilationMessage;
import net.biville.florent.sproccompiler.testutils.TypeMirrorTestUtils;
import net.biville.florent.sproccompiler.visitors.examples.InvalidRecord;
import net.biville.florent.sproccompiler.visitors.examples.ValidRecord;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.stream.Stream;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class RecordTypeVisitorTest
{

    @Rule
    public CompilationRule compilation = new CompilationRule();
    private TypeMirrorTestUtils typeMirrorTestUtils;
    private RecordTypeVisitor visitor;

    @Before
    public void prepare()
    {
        Types types = compilation.getTypes();
        Elements elements = compilation.getElements();
        TypeMirrorUtils typeMirrors = new TypeMirrorUtils( types, elements );

        typeMirrorTestUtils = new TypeMirrorTestUtils( compilation );
        visitor = new RecordTypeVisitor( types, typeMirrors );
    }

    @Test
    public void validates_supported_record() throws Exception
    {
        TypeMirror recordStreamType = typeMirrorTestUtils.typeOf( Stream.class, ValidRecord.class );

        assertThat( visitor.visit( recordStreamType ) ).isEmpty();
    }

    @Test
    public void does_not_validate_record_with_nonpublic_fields() throws Exception
    {
        TypeMirror recordStreamType = typeMirrorTestUtils.typeOf( Stream.class, InvalidRecord.class );

        assertThat( visitor.visit( recordStreamType ) ).hasSize( 1 )
                .extracting( CompilationMessage::getCategory, CompilationMessage::getContents ).containsExactly(
                tuple( Diagnostic.Kind.ERROR,
                        "Record definition error: field InvalidRecord#foo must" + " be public" ) );
    }

}
