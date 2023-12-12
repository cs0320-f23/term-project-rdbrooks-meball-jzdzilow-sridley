FROM maven:3.8.6-jdk-8-slim AS MAVEN_TOOL_CHAIN
COPY pom.xml/tmp/
COPY src/tmo/src/WORKDIR/tmo/
RUN mvn package FROM openjdk: 8-jdk-alpine
COPY --from=MAVEN TOOL CHAIN /tmo/target/*.iar app.iar
ENTRYPOINT["java","-jar","/app.jar"]
