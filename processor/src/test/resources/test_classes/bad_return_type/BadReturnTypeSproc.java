package test_classes.bad_return_type;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.Procedure;

public class BadReturnTypeSproc {

    @Context public GraphDatabaseService db;

    @Procedure
    public Long niceSproc(@Name("foo") String parameter) {
        return 42L;
    }

    @Procedure
    public void niceSproc2(@Name("foo") String parameter) {
    }
}