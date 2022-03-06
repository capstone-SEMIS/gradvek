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

### To check app health:
GET to /info

### To add entity to the neo4j database manually:
POST to the /upload endpoint. The payload is the JSON representation of the entity.
