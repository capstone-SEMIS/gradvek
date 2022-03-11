FROM adoptopenjdk/openjdk11:latest
ARG JAR_FILE=../springdb/target/*.jar
ARG FRONT_END=../springdb/frontend
ARG ENTRY=start.sh
COPY ${JAR_FILE} app.jar
COPY ${FRONT_END} frontend
COPY ${ENTRY} start.sh
ENTRYPOINT ["start.sh"]