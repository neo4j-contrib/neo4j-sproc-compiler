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

import net.biville.florent.sproccompiler.errors.CompilationError;
import net.biville.florent.sproccompiler.errors.ContextFieldError;
import org.neo4j.procedure.Context;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.SimpleElementVisitor8;
import java.util.Set;
import java.util.stream.Stream;

public class ContextFieldVisitor extends SimpleElementVisitor8<Stream<CompilationError>, Void> {

    @Override
    public Stream<CompilationError> visitVariable(VariableElement field, Void ignored) {
        Set<Modifier> modifiers = field.getModifiers();
        if (!modifiers.contains(Modifier.PUBLIC)
                || modifiers.contains(Modifier.STATIC)
                || modifiers.contains(Modifier.FINAL)) {

            return Stream.of(new ContextFieldError(
                    field,
                    "@%s usage error: field %s#%s should be public, non-static and non-final",
                    Context.class.getName(),
                    field.getEnclosingElement().getSimpleName(),
                    field.getSimpleName()
            ));
        }
        return Stream.empty();
    }
}
