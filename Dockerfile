FROM adoptopenjdk/openjdk11:latest
ARG JAR_FILE=springdb/target/*.jar
ARG FRONT_END=springdb/frontend
ARG ENTRY=start.sh
COPY ${JAR_FILE} app.jar
COPY ${FRONT_END} frontend
COPY ${ENTRY} start.sh
RUN apt-get update && apt-get install -y npm
RUN cd frontend && npm install n && node_modules/n/bin/n stable
ENTRYPOINT ["/start.sh"]