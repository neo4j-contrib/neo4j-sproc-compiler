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
import net.biville.florent.sproccompiler.io.CsvWriter;
import net.biville.florent.sproccompiler.visitors.ExecutableElementVisitor;
import org.neo4j.procedure.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

@AutoService( Processor.class )
public class CsvProcessor extends AbstractProcessor {

    private static final String DOCUMENTATION_ROOT_PATH = "GeneratedDocumentationPath";
    private final Map<PackageElement, Collection<ExecutableElement>> apocCatalog = new HashMap<>();
    private Elements elementUtils;
    private ElementVisitor<ExecutableElement, Void> methodVisitor;
    private Optional<Path> rootPath;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<>(asList(Procedure.class.getName(), UserFunction.class.getName()));
    }

    @Override
    public Set<String> getSupportedOptions() {
        return Collections.singleton(DOCUMENTATION_ROOT_PATH);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        apocCatalog.clear();
        elementUtils = processingEnv.getElementUtils();
        methodVisitor = new ExecutableElementVisitor(processingEnv.getMessager());
        rootPath = Optional.ofNullable(processingEnv.getOptions().getOrDefault(DOCUMENTATION_ROOT_PATH, null))
                        .map(Paths::get);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        rootPath.ifPresent((root) -> {
            if (!roundEnv.processingOver()) {
                roundEnv.getElementsAnnotatedWith(Procedure.class).forEach(this::index);
                roundEnv.getElementsAnnotatedWith(UserFunction.class).forEach(this::index);

            } else {
                generateDocumentation(root);
            }
        });
        

        return false;
    }

    private void index(Element el) {
        ExecutableElement method = methodVisitor.visit(el);
        apocCatalog.computeIfAbsent(elementUtils.getPackageOf(method), (k) -> new ArrayList<>()).add(method);
    }

    private void generateDocumentation(Path root) {
        apocCatalog.entrySet().forEach(kv -> {
            generatePackageDocumentation(root, kv);
        });
    }

    private void generatePackageDocumentation(Path root, Map.Entry<PackageElement, Collection<ExecutableElement>> kv) {
        PackageElement packageElement = kv.getKey();
        File file = new File(root.toFile(), packageElement.getSimpleName() + ".csv");

        List<String> header = asList("name", "description", "execution mode", "location", "deprecated by");
        try (FileWriter resource = new FileWriter(file);
             CsvWriter writer = new CsvWriter(header, resource)) {

            writer.write(kv.getValue().stream(), this::generateCallableDocumentation);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private Stream<String> generateCallableDocumentation(ExecutableElement method) {
        return Stream.of(
                name(method),
                description(method),
                executionMode(method),
                location(method),
                deprecatedBy(method)
        );
    }

    private String name(ExecutableElement method) {
        return String.format("%s(%s)", callableName(method), parameters(method));
    }

    private String description(ExecutableElement method) {
        Description description = method.getAnnotation(Description.class);
        if (description == null) {
            return "";
        }
        return description.value();
    }

    private String executionMode(ExecutableElement method) {
        PerformsWrites performsWrites = method.getAnnotation(PerformsWrites.class);
        if (performsWrites != null) {
            return "PERFORMS_WRITE";
        }
        Procedure procedure = method.getAnnotation(Procedure.class);
        if (procedure != null) {
            return procedure.mode().name();
        }
        return "N/A";
    }

    private String location(ExecutableElement method) {
        return String.format("%s.%s",
                elementUtils.getPackageOf(method).getQualifiedName(),
                method.getEnclosingElement().getSimpleName());
    }

    private String deprecatedBy(ExecutableElement method) {
        UserFunction function = method.getAnnotation(UserFunction.class);
        if (function != null) {
            String deprecatedBy = function.deprecatedBy();
            return deprecatedBy.isEmpty() ? "N/A" : deprecatedBy;
        }
        String deprecatedBy = method.getAnnotation(Procedure.class).deprecatedBy();
        return deprecatedBy.isEmpty() ? "N/A" : deprecatedBy;
    }

    private String callableName(ExecutableElement method) {
        Supplier<String> defaultName = () -> elementUtils.getPackageOf(method).getQualifiedName() + "." + method.getSimpleName();
        UserFunction function = method.getAnnotation(UserFunction.class);
        if (function != null) {
            return UserFunctionProcessor.getCustomName(function).orElseGet(defaultName);
        }
        Procedure procedure = method.getAnnotation(Procedure.class);
        return ProcedureProcessor.getCustomName(procedure).orElseGet(defaultName);
    }

    private String parameters(ExecutableElement method) {
        return method.getParameters().stream()
                .map(this::parameterSignature)
                .collect(Collectors.joining(","));
    }

    private String parameterSignature(VariableElement param) {
        return param.asType().toString().replace("java.lang.", "") + " " + param.getAnnotation(org.neo4j.procedure.Name.class).value();
    }

}
