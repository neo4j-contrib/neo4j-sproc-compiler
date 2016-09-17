/*
 * Copyright 2016-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
