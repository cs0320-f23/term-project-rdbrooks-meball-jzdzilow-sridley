FROM maven:latest
WORKDIR back
RUN [ "mvn", "package"]
RUN [ "mvn", "exec:java"]
