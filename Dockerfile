FROM --platform=linux/amd64 eclipse-temurin:11 AS build
ARG JAR_FILE=springdb/target/*.jar
ARG FRONT_END=springdb/frontend
ARG NODE_NPM=springdb/target/node
ARG ENTRY=start.sh
COPY ${JAR_FILE} app.jar
COPY ${FRONT_END} frontend
COPY ${NODE_NPM} node
COPY ${ENTRY} start.sh
ENTRYPOINT ["/start.sh"]