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

import net.biville.florent.sproccompiler.messages.CompilationMessage;
import net.biville.florent.sproccompiler.messages.PerformsWriteMisuseError;

import java.util.stream.Stream;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.util.SimpleElementVisitor8;

import org.neo4j.procedure.Mode;
import org.neo4j.procedure.PerformsWrites;
import org.neo4j.procedure.Procedure;

public class PerformsWriteMethodVisitor extends SimpleElementVisitor8<Stream<CompilationMessage>,Void>
{

    @Override
    public Stream<CompilationMessage> visitExecutable( ExecutableElement method, Void ignored )
    {
        Procedure procedure = method.getAnnotation( Procedure.class );
        if ( procedure == null )
        {
            return Stream.of( new PerformsWriteMisuseError( method, "@%s usage error: missing @%s annotation on method",
                    PerformsWrites.class.getSimpleName(), Procedure.class.getSimpleName() ) );
        }

        if ( procedure.mode() != Mode.DEFAULT )
        {
            return Stream.of( new PerformsWriteMisuseError( method,
                    "@%s usage error: cannot use mode other than Mode.DEFAULT",
                    PerformsWrites.class.getSimpleName() ) );
        }
        return Stream.empty();
    }

}
