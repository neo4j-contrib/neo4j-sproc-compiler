package io.github.fbiville.sproccompiler;

import com.google.auto.service.AutoService;
import org.neo4j.procedure.Procedure;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static javax.tools.Diagnostic.Kind.ERROR;

@AutoService(Processor.class)
public class SprocCompiler extends AbstractProcessor {

    private static final Class<? extends Annotation> sprocType = Procedure.class;
    private final ElementVisitor<Stream<CompilationError>, Void> parameterVisitor;
    private Messager messager;

    public SprocCompiler() {
        parameterVisitor = new ParameterVisitor();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(sprocType.getName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {

        roundEnv.getElementsAnnotatedWith(sprocType)
                .stream()
                .flatMap(this::validate)
                .forEachOrdered(error -> {
                    messager.printMessage(
                            ERROR,
                            error.getErrorMessage(),
                            error.getElement(),
                            error.getMirror()
                    );
                });

        return false;
    }

    private Stream<CompilationError> validate(Element element) {
        return parameterVisitor.visit(element);
    }
}
