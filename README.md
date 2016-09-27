# reveal-role-dynamic

This is the dynamic role analysis component of the REVEAL project.

## Compile & package

    $ mvn package

This creates the JAR file
'target/reveal-role-dynamic-1.2-jar-with-dependencies.jar', which is
self-contained, including all Java dependencies.

## Usage

    $ java -jar target/reveal-role-dynamic-1.2-jar-with-dependencies.jar $DATABASE_NAME

where $DATABASE_NAME is the name of the database within ATC's MongoDB to use, e.g., 'nuclear_test' or 'us_elections', etc.  

When run, the program will read content from the given MongoDB database, compute roles for all users, and write out the computed roles into the table 'Role' in the same database.  The main function exists when all roles have been written to the database. 

The component must have write access to the database.
