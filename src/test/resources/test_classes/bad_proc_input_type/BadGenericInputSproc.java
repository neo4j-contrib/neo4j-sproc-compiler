package test_classes.bad_proc_input_type;

import org.neo4j.procedure.Procedure;
import org.neo4j.procedure.Name;
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
}