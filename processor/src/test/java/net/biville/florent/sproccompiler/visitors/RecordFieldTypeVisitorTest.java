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
package net.biville.florent.sproccompiler.visitors;

import com.google.testing.compile.CompilationRule;
import net.biville.florent.sproccompiler.compilerutils.TypeMirrorUtils;
import net.biville.florent.sproccompiler.testutils.TypeMirrorTestUtils;
import org.junit.Before;
import org.junit.Rule;

import javax.lang.model.type.TypeVisitor;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class RecordFieldTypeVisitorTest extends TypeValidationTestSuite
{

    @Rule
    public CompilationRule compilationRule = new CompilationRule();
    private Types types;
    private TypeMirrorUtils typeMirrorUtils;
    private TypeMirrorTestUtils typeMirrorTestUtils;

    @Before
    public void prepare()
    {
        Elements elements = compilationRule.getElements();
        types = compilationRule.getTypes();
        typeMirrorUtils = new TypeMirrorUtils( types, elements );
        typeMirrorTestUtils = new TypeMirrorTestUtils( compilationRule );
    }

    @Override
    protected TypeVisitor<Boolean,Void> visitor()
    {
        return new RecordFieldTypeVisitor( types, typeMirrorUtils );
    }

    @Override
    protected TypeMirrorTestUtils typeMirrorTestUtils()
    {
        return typeMirrorTestUtils;
    }
}
