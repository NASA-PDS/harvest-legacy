# Search Service
The Search Service package provides functionality for search and discovery
of the PDS holdings. The functionality for this service is satisfied by the 
Apache Solr package. The software is packaged in a WAR file.

# Build
The software can be compiled with the "mvn compile" command but in order 
to create the WAR file, you must execute the "mvn compile war:war" command. 
The documentation including release notes, installation and operation of the 
software should be online at 
http://pds-cm.jpl.nasa.gov/pds4/search/search-service/. If it is not 
accessible, you can execute the "mvn site:run" command and view the 
documentation locally at http://localhost:8080.

In order to create a complete package for distribution, execute the 
following commands: 

```
% mvn site
% mvn package
```
