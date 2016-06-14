package test_classes.bad_record_type;

import org.neo4j.procedure.Procedure;

import java.util.stream.Stream;

public class BadRecordTypeSproc {

    @Procedure
    public Stream<BadRecord> doIt() {
        return Stream.of(new BadRecord("bad", 42));
    }
}
