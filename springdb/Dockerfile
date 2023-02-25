FROM --platform=linux/arm64/v8 eclipse-temurin:11 AS build
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
