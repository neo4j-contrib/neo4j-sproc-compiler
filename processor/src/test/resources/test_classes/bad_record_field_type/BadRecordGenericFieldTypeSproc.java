package test_classes.bad_record_field_type;

import org.neo4j.procedure.Procedure;

import java.util.stream.Stream;

public class BadRecordGenericFieldTypeSproc {

    @Procedure
    public Stream<BadRecordGenericFieldType> doSomething() {
        return Stream.empty();
    }
}
