package net.biville.florent.sproccompiler.procedures.valid;

import org.neo4j.procedure.Description;
import org.neo4j.procedure.Mode;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.Procedure;
import org.neo4j.procedure.UserFunction;

public class OrderedFunctions
{

    @UserFunction(name="proc.sum")
    public long sum( @Name( "a" ) int operand1, @Name( "b" ) int operand2 )
    {
        return operand1 + operand2;
    }

    @UserFunction(name="proc.add")
    public long add( @Name( "a" ) int operand1, @Name( "b" ) int operand2 )
    {
        return operand1 + operand2;
    }

    @UserFunction(name="proc.multiply")
    public long multiply( @Name( "a" ) int operand1, @Name( "b" ) int operand2 )
    {
        return operand1 * operand2;
    }
}
