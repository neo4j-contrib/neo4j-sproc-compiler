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

import com.google.auto.service.AutoService;
import net.biville.florent.sproccompiler.errors.CompilationError;
import net.biville.florent.sproccompiler.errors.ErrorPrinter;
import net.biville.florent.sproccompiler.visitors.ContextFieldVisitor;
import org.neo4j.procedure.Context;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@AutoService(Processor.class)
public class ContextProcessor extends AbstractProcessor {

    private static final Class<? extends Annotation> contextType = Context.class;
    private ElementVisitor<Stream<CompilationError>, Void> contextFieldVisitor;
    private ErrorPrinter errorPrinter;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(contextType.getName());
        return types;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        errorPrinter = new ErrorPrinter(processingEnv.getMessager());
        contextFieldVisitor = new ContextFieldVisitor();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        processStoredProcedureContextFields(roundEnv);
        return false;
    }

    private void processStoredProcedureContextFields(RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(contextType)
                .stream()
                .flatMap(this::validateContextField)
                .forEachOrdered(errorPrinter::print);

    }

    private Stream<CompilationError> validateContextField(Element element) {
        return contextFieldVisitor.visit(element);
    }


}
