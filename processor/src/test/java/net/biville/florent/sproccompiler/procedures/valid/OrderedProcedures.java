package net.biville.florent.sproccompiler.procedures.valid;

import org.neo4j.procedure.Mode;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.Procedure;

public class OrderedProcedures
{

    @Procedure( mode = Mode.SCHEMA, name="proc.aName" )
    public void doSomething1( @Name( "bar" ) long bar )
    {

    }

    @Procedure( mode = Mode.SCHEMA, name="proc.cName.foo" )
    public void doSomething3( @Name( "bar" ) long bar )
    {

    }

    @Procedure( mode = Mode.SCHEMA, name="proc.bName" )
    public void doSomething2( @Name( "bar" ) long bar )
    {

    }
}
