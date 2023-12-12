FROM maven:latest
RUN [ "mvn", "exec:java"]
