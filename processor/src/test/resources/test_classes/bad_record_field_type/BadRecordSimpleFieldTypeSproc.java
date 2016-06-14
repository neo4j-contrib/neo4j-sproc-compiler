package test_classes.bad_record_field_type;

import org.neo4j.procedure.Procedure;

import java.util.stream.Stream;

public class BadRecordSimpleFieldTypeSproc {

    @Procedure
    public Stream<BadRecordSimpleFieldType> doSomething() {
        return Stream.empty();
    }
}
