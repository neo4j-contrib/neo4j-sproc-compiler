package test_classes.duplicated;

import org.neo4j.procedure.Procedure;

public class Sproc1 {

    @Procedure
    public void foobar() {

    }

    @Procedure
    public void foobarbaz() {

    }
}
