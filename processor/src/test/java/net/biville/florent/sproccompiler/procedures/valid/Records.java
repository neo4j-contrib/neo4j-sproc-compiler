/*
 * Copyright 2016-2018 the original author or authors.
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
package net.biville.florent.sproccompiler.procedures.valid;

import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;

public class Records
{

    public static class LongWrapper
    {
        public final Long value;

        public LongWrapper( Long value )
        {
            this.value = value;
        }
    }

    public static class SimpleTypesWrapper
    {
        public String field01 = "string";
        public long field02 = 2;
        public Long field03 = 3L;
        public Number field04 = 4.0;
        public Boolean field05 = true;
        public boolean field06 = true;
        public Object field07;
        public Node field08;
        public Path field09;
        public Relationship field10;
    }

    public static class GenericTypesWrapper
    {
        public List<String> field01;
        public List<Long> field03;
        public List<Number> field04;
        public List<Boolean> field05;
        public List<Object> field07;
        public List<Node> field08;
        public List<Path> field09;
        public List<Relationship> field10;
        public Map<String,String> field11;
        public Map<String,Long> field13;
        public Map<String,Number> field14;
        public Map<String,Boolean> field15;
        public Map<String,Object> field17;
        public Map<String,Node> field18;
        public Map<String,Path> field19;
        public Map<String,Relationship> field20;
        public List<List<Relationship>> field21;
        public List<Map<String,Relationship>> field22;
    }
}
