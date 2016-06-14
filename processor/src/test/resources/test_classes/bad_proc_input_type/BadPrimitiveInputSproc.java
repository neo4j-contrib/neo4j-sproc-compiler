package test_classes.bad_proc_input_type;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.Procedure;

public class BadPrimitiveInputSproc {

    @Procedure
    public void doSomething(@Name("test") short unsupportedType) {
        
    }

    @Procedure public void works01(@Name("test") String supported) {}
    @Procedure public void works02(@Name("test") Long supported) {}
    @Procedure public void works03(@Name("test") long supported) {}
    @Procedure public void works04(@Name("test") Double supported) {}
    @Procedure public void works05(@Name("test") double supported) {}
    @Procedure public void works06(@Name("test") Number supported) {}
    @Procedure public void works07(@Name("test") Boolean supported) {}
    @Procedure public void works08(@Name("test") boolean supported) {}
    @Procedure public void works09(@Name("test") Object supported) {}
    @Procedure public void works10(@Name("test") Node supported) {}
    @Procedure public void works11(@Name("test") Relationship supported) {}
    @Procedure public void works12(@Name("test") Path supported) {}
}