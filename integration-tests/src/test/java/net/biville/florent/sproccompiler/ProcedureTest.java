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
package net.biville.florent.sproccompiler;

import net.biville.florent.sproccompiler.procedures.valid.Procedures;
import org.junit.Rule;
import org.junit.Test;

import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.harness.junit.Neo4jRule;

import static org.assertj.core.api.Assertions.assertThat;


public class ProcedureTest
{

    private static final Class<?> PROCEDURES_CLASS = Procedures.class;

    @Rule
    public Neo4jRule graphDb = new Neo4jRule().withProcedure( PROCEDURES_CLASS );
    private String procedureNamespace = PROCEDURES_CLASS.getPackage().getName();

    @Test
    public void calls_simplistic_procedure()
    {
        try ( Driver driver = GraphDatabase.driver( graphDb.boltURI(), configuration() );
                Session session = driver.session() )
        {

            StatementResult result = session.run( "CALL " + procedureNamespace + ".theAnswer()" );

            assertThat( result.single().get( "value" ).asLong() ).isEqualTo( 42L );
        }
    }

    @Test
    public void calls_procedures_with_simple_input_type_returning_void()
    {
        try ( Driver driver = GraphDatabase.driver( graphDb.boltURI(), configuration() );
                Session session = driver.session() )
        {

            session.run( "CALL " + procedureNamespace + ".simpleInput00()" );
            session.run( "CALL " + procedureNamespace + ".simpleInput01('string')" );
            session.run( "CALL " + procedureNamespace + ".simpleInput02(42)" );
            session.run( "CALL " + procedureNamespace + ".simpleInput03(42)" );
            session.run( "CALL " + procedureNamespace + ".simpleInput04(4.2)" );
            session.run( "CALL " + procedureNamespace + ".simpleInput05(true)" );
            session.run( "CALL " + procedureNamespace + ".simpleInput06(false)" );
            session.run( "CALL " + procedureNamespace + ".simpleInput07({foo:'bar'})" );
            session.run( "MATCH (n)            CALL " + procedureNamespace + ".simpleInput08(n) RETURN n" );
            session.run( "MATCH p=(()-[r]->()) CALL " + procedureNamespace + ".simpleInput09(p) RETURN p" );
            session.run( "MATCH ()-[r]->()     CALL " + procedureNamespace + ".simpleInput10(r) RETURN r" );
        }
    }

    @Test
    public void calls_procedures_with_simple_input_type_returning_record_with_primitive_fields()
    {
        try ( Driver driver = GraphDatabase.driver( graphDb.boltURI(), configuration() );
                Session session = driver.session() )
        {

            assertThat( session.run( "CALL " + procedureNamespace + ".simpleInput11('string')" ).single() ).isNotNull();
            assertThat( session.run( "CALL " + procedureNamespace + ".simpleInput12(42)" ).single() ).isNotNull();
            assertThat( session.run( "CALL " + procedureNamespace + ".simpleInput13(42)" ).single() ).isNotNull();
            assertThat( session.run( "CALL " + procedureNamespace + ".simpleInput14(4.2)" ).single() ).isNotNull();
            assertThat( session.run( "CALL " + procedureNamespace + ".simpleInput15(true)" ).single() ).isNotNull();
            assertThat( session.run( "CALL " + procedureNamespace + ".simpleInput16(false)" ).single() ).isNotNull();
            assertThat( session.run( "CALL " + procedureNamespace + ".simpleInput17({foo:'bar'})" ).single() )
                    .isNotNull();
            assertThat( session.run( "CALL " + procedureNamespace + ".simpleInput21()" ).single() ).isNotNull();
        }

    }

    private Config configuration()
    {
        return Config.build().withEncryptionLevel( Config.EncryptionLevel.NONE ).toConfig();
    }

}
