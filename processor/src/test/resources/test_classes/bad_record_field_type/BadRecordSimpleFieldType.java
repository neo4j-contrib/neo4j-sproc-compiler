package test_classes.bad_record_field_type;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;

public class BadRecordSimpleFieldType {

    public Integer wrongType;
    public String okType1;
    public Long okType2;
    public long okType3;
    public Double okType4;
    public double okType5;
    public Number okType6;
    public Boolean okType7;
    public boolean okType8;
    public Path okType9;
    public Node okType10;
    public Relationship okType11;
    public Object okType12;
}
