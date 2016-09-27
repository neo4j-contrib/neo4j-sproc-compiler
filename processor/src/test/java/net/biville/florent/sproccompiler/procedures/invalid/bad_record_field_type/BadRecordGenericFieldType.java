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
package net.biville.florent.sproccompiler.procedures.invalid.bad_record_field_type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;

public class BadRecordGenericFieldType
{

    public Map<String,Integer> wrongType1;
    public List<Integer> wrongType2;
    public List<List<Map<String,Integer>>> wrongType3;
    public List<String> okType1;
    public List<Long> okType2;
    public List<Double> okType4;
    public List<Number> okType6;
    public List<Boolean> okType7;
    public List<Path> okType9;
    public List<Node> okType10;
    public List<Relationship> okType11;
    public List<Object> okType12;
    public Map<String,Object> okType13;
    public HashMap<String,Object> okType14;
    public ArrayList<Boolean> okType15;
    public ArrayList<Object> okType16;
}
