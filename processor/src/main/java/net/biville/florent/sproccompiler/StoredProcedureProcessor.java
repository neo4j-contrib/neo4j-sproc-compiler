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
import net.biville.florent.sproccompiler.validators.DuplicatedStoredProcedureValidator;
import net.biville.florent.sproccompiler.visitors.ContextFieldVisitor;
import net.biville.florent.sproccompiler.visitors.StoredProcedureVisitor;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Procedure;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static javax.tools.Diagnostic.Kind.ERROR;

@AutoService(Processor.class)
public class StoredProcedureProcessor extends AbstractProcessor {

    private static final Class<? extends Annotation> sprocType = Procedure.class;
    private static final Class<? extends Annotation> contextType = Context.class;
    private final Set<Element> visitedProcedures = new LinkedHashSet<>();

    private Function<Collection<Element>, Stream<CompilationError>> duplicateProcedure;
    private ElementVisitor<Stream<CompilationError>, Void> parameterVisitor;
    private ElementVisitor<Stream<CompilationError>, Void> contextFieldVisitor;
    private Messager messager;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(sprocType.getName());
        types.add(contextType.getName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        Types typeUtils = processingEnv.getTypeUtils();
        Elements elementUtils = processingEnv.getElementUtils();

        visitedProcedures.clear();
        duplicateProcedure = new DuplicatedStoredProcedureValidator();
        messager = processingEnv.getMessager();
        parameterVisitor = new StoredProcedureVisitor(typeUtils, elementUtils);
        contextFieldVisitor = new ContextFieldVisitor();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {

        processStoredProcedures(roundEnv);
        processStoredProcedureContextFields(roundEnv);
        if (roundEnv.processingOver()) {
            duplicateProcedure.apply(visitedProcedures)
                    .forEach(this::printError);
        }
        return false;
    }

    private void processStoredProcedures(RoundEnvironment roundEnv) {
        Set<? extends Element> procedures = roundEnv.getElementsAnnotatedWith(sprocType);
        visitedProcedures.addAll(procedures);
        procedures
                .stream()
                .flatMap(this::validateStoredProcedure)
                .forEachOrdered(this::printError);
    }

    private void processStoredProcedureContextFields(RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(contextType)
                .stream()
                .flatMap(this::validateContextField)
                .forEachOrdered(this::printError);

    }

    private Stream<CompilationError> validateStoredProcedure(Element element) {
        return parameterVisitor.visit(element);
    }

    private Stream<CompilationError> validateContextField(Element element) {
        return contextFieldVisitor.visit(element);
    }

    private void printError(CompilationError error) {
        messager.printMessage(
                ERROR,
                error.getErrorMessage(),
                error.getElement(),
                error.getMirror()
        );
    }
}
