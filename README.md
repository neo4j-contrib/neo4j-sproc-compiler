# Neo4j stored procedure compiler

This is a annotation processor that will verify your stored procedures
at compile time.

While most of the basic checks can be performed, you still need
some unit tests to verify some runtime behaviours.

# Use the processor

TODO

# Remaining tasks

 - get stored proc formal spec and correct assumptions if needed
 - check @Context fields are non-final and public
 - check stored proc unicity within the scanned project
 - write integration test for the processor
