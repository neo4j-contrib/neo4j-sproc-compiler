package test_classes.bad_proc_input_type;

import org.neo4j.procedure.Procedure;
import org.neo4j.procedure.Name;

public class BadPrimitiveInputSproc {

    @Procedure
    public void doSomething(@Name("test") short unsupportedType) {
        
    }

    @Procedure public void works1(@Name("test") String supported) {}
    @Procedure public void works2(@Name("test") Long supported) {}
    @Procedure public void works3(@Name("test") long supported) {}
    @Procedure public void works4(@Name("test") Double supported) {}
    @Procedure public void works5(@Name("test") double supported) {}
    @Procedure public void works6(@Name("test") Number supported) {}
    @Procedure public void works7(@Name("test") Boolean supported) {}
    @Procedure public void works8(@Name("test") boolean supported) {}
    @Procedure public void works9(@Name("test") Object supported) {}
}