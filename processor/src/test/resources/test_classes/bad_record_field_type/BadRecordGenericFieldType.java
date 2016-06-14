package test_classes.bad_record_field_type;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BadRecordGenericFieldType {

    public Map<String, Integer> wrongType1;
    public List<Integer> wrongType2;
    public List<List<Map<String, Integer>>> wrongType3;
    public List<String> okType1;
    public List<Long> okType2;
    public List<Double> okType4;
    public List<Number> okType6;
    public List<Boolean> okType7;
    public List<Path> okType9;
    public List<Node> okType10;
    public List<Relationship> okType11;
    public List<Object> okType12;
    public Map<String, Object> okType13;
    public HashMap<String, Object> okType14;
    public ArrayList<Boolean> okType15;
    public ArrayList<Object> okType16;
}
