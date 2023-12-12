FROM maven:latest
WORKDIR /back
RUN ["ls"]
RUN [ "mvn", "package"]
RUN [ "mvn", "exec:java"]
