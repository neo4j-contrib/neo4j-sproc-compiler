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
import org.junit.Rule;
import org.junit.Test;

import javax.annotation.processing.Processor;
import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class PerformsWriteProcessorTest
{
    @Rule
    public CompilationRule compilation = new CompilationRule();

    private Processor processor = new PerformsWriteProcessor();

    @Test
    public void fails_with_conflicting_mode() throws Exception
    {
        JavaFileObject procedure = JavaFileObjectUtils.INSTANCE.procedureSource(
                "invalid/conflicting_mode/ConflictingMode.java" );

        assert_().about( javaSource() ).that( procedure ).processedWith( processor ).failsToCompile()
                .withErrorCount( 1 )
                .withErrorContaining( "@PerformsWrites usage error: cannot use mode other than Mode.DEFAULT" )
                .in( procedure ).onLine( 26 );

    }
}
