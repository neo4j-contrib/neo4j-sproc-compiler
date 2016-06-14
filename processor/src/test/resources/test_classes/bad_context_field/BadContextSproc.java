package test_classes.bad_context_field;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.procedure.Context;

public class BadContextSproc {

    @Context public GraphDatabaseService db;
    @Context protected GraphDatabaseService shouldBePublic;
    @Context public static GraphDatabaseService shouldBeNonStatic;
    @Context public final GraphDatabaseService shouldBeNonFinal = null;
}
