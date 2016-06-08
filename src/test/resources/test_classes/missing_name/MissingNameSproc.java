package test_classes.missing_name;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Procedure;

import java.util.stream.Stream;

public class MissingNameSproc {

    public static class GoodRecord {
        public long age;
    }

    @Context public GraphDatabaseService db;

    @Procedure
    public Stream<GoodRecord> niceSproc(String parameter, String otherParam) {
        return Stream.empty();
    }
}