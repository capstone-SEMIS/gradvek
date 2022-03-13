# The Spring DB application
### Intro
Provides the layer between the OpenTarget data and the neo4j graph database
### To build:
The app is dockerized. Do
```
cd springdb
mvn install
docker build -t gradvek/springdb-docker .
```
### To run:
The app runs on port 8080. Remap if needed.
```
docker run -p 8080:8080 gradvek/springdb-docker
```
Outside of docker, do
```
./mvnw spring-boot:run
```

Please note that the bridge to Neo4j has different syntax depending on whether you're running directly or in a container - see next item.

### To enable connection to the Neo4j server:
Modify the src/resources/application.properties, specifying the following:
```
neo4j.url=bolt://host.docker.internal:7687 # to run in docker
neo4j.url=bolt://localhost:7687 # to run with ./mvnw spring-boot:run
neo4j.user=neo4j #default user
neo4j.password=<password> #default pwd is "neo4j", but it'll demand to change it on first login
```
## REST API

### To check app health:
```
http://localhost:8080/info
```

### To initialize with demo data:
```
curl --header "Content-Type: application/json"   --request POST   --data '{}'   http://localhost:8080/init/demo
```

### To manually add a single gene to the database
```
curl --header "Content-Type: application/json"   --request POST   --data '{}'   http://localhost:8080/gene/<gene id>
```

### To import all targets from OpenTargets
```
curl --header "Content-Type: application/json"   --request POST   --data '{}'   http://localhost:8080/init/targets
```

### To clean the db (WILL WIPE OUT EVERYTHING)
```
curl --header "Content-Type: application/json"   --request POST   --data '{}'   http://localhost:8080/clear
```

### To run the frontend on localhost:3000
```
cd frontend
npm install 
npm run start
```
