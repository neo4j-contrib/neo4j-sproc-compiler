package test_classes.working_procedures;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;

import java.util.List;
import java.util.Map;

public class Records {

    public static class LongWrapper {
        public final Long value;

        public LongWrapper(Long value) {
            this.value = value;
        }
    }

    public static class SimpleTypesWrapper {
        public String           field01 = "string";
        public long             field02 = 2;
        public Long             field03 = 3L;
        public Number           field04 = 4.0;
        public Boolean          field05 = true;
        public boolean          field06 = true;
        public Object           field07;
        public Node             field08;
        public Path             field09;
        public Relationship     field10;
    }

    public static class GenericTypesWrapper {
        public List<String>             field01;
        public List<Long>               field03;
        public List<Number>             field04;
        public List<Boolean>            field05;
        public List<Object>             field07;
        public List<Node>               field08;
        public List<Path>               field09;
        public List<Relationship>       field10;
        public Map<String,String>       field11;
        public Map<String,Long>         field13;
        public Map<String,Number>       field14;
        public Map<String,Boolean>      field15;
        public Map<String,Object>       field17;
        public Map<String,Node>         field18;
        public Map<String,Path>         field19;
        public Map<String,Relationship> field20;
        public List<List<Relationship>> field21;
        public List<Map<String,Relationship>> field22;
    }
}
