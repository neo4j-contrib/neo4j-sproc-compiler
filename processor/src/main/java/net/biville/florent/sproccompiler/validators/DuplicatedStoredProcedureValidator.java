package net.biville.florent.sproccompiler.validators;

import net.biville.florent.sproccompiler.errors.CompilationError;
import net.biville.florent.sproccompiler.errors.DuplicatedProcedureError;

import javax.lang.model.element.Element;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;

public class DuplicatedStoredProcedureValidator implements Function<Collection<Element>, Stream<CompilationError>> {

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
