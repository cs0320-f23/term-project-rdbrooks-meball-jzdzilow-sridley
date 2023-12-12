FROM maven:latest
WORKDIR /back
RUN [ "mvn", "exec:java"]
