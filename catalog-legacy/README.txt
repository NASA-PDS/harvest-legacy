The Catalog Tool is a command-line application for comparing,
validating and ingesting PDS3 catalog submissions in the form of 
catalog files into the Registry and Storage Services.

The software is packaged in a JAR file with corresponding shell scripts 
for launching the application.

The software can be compiled with the "mvn compile" command but in order 
to create the JAR file, you must execute the "mvn package" command. The 
documentation including release notes, installation and operation of the 
software should be online at http://pds-cm.jpl.nasa.gov/pds4/ingest/catalog/. 
If it is not accessible, you can execute the "mvn site:run" command and 
view the documentation locally at http://localhost:8080.

In order to create a complete distribution package, execute the 
following commands: 

% mvn site
% mvn package
