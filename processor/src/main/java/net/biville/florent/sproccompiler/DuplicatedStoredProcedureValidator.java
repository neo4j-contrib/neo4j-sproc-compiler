package net.biville.florent.sproccompiler;

import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;

class DuplicatedStoredProcedureValidator implements Function<Collection<Element>, Stream<CompilationError>> {

    private final Types typeUtils;
    private final Elements elementUtils;

    public DuplicatedStoredProcedureValidator(Types typeUtils, Elements elementUtils) {
        this.typeUtils = typeUtils;
        this.elementUtils = elementUtils;
    }

    @Override
    public Stream<CompilationError> apply(Collection<Element> visitedProcedures) {
        return findDuplicates(visitedProcedures);
    }

    private Stream<CompilationError> findDuplicates(Collection<Element> visitedProcedures) {
        return indexByLocation(visitedProcedures)
                .filter(c -> c.getValue().size() > 1)
                .map(this::asError);
    }

    private Stream<Map.Entry<ProcedureSourceLocation, List<VisitedProcedureMapEntry>>> indexByLocation(Collection<Element> visitedProcedures) {
        return visitedProcedures.stream()
                .map(VisitedProcedureMapEntry::new)
                .collect(groupingBy(VisitedProcedureMapEntry::key))
                .entrySet().stream();
    }

    private CompilationError asError(Map.Entry<ProcedureSourceLocation, List<VisitedProcedureMapEntry>> entry) {
        ProcedureSourceLocation duplicatedName = entry.getKey();
        List<VisitedProcedureMapEntry> duplicates = entry.getValue();
        Element packageElement = duplicates.get(0).value().getEnclosingElement().getEnclosingElement();
        return duplicationError(
                duplicatedName,
                duplicates.size(),
                packageElement,
                duplicates.stream()
                        .map(dupe -> dupe.value().getEnclosingElement().getSimpleName().toString())
                        .collect(Collectors.joining(","))
        );
    }

    private DuplicatedProcedureError duplicationError(ProcedureSourceLocation duplicateLocation,
                                                      int size,
                                                      Element element,
                                                      String classes) {
        return new DuplicatedProcedureError(
                element,
                "Package <%s> contains %s definitions of procedure <%s>. Offending classes: <%s>",
                duplicateLocation.packageName(),
                String.valueOf(size),
                duplicateLocation.methodName(),
                classes
        );
    }
}
