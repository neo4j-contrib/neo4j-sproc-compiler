package test_classes.bad_proc_input_type;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.Procedure;

import java.util.List;
import java.util.Map;

public class BadGenericInputSproc {

    @Procedure
    public void doSomething(@Name("test") List<List<Map<String, Thread>>> unsupportedType) {
        
    }

    @Procedure
    public void doSomething2(@Name("test") Map<String, List<Object>> unsupportedType) {

    }

    @Procedure public void works1(@Name("test") List<String> supported) {}
    @Procedure public void works2(@Name("test") List<List<Object>> supported) {}
    @Procedure public void works3(@Name("test") Map<String, Object> supported) {}
    @Procedure public void works4(@Name("test") List<List<List<Map<String, Object>>>> supported) {}
    @Procedure public void works5(@Name("test") List<List<List<Path>>> supported) {}
    @Procedure public void works6(@Name("test") List<Node> supported) {}
    @Procedure public void works7(@Name("test") List<List<Relationship>> supported) {}
}