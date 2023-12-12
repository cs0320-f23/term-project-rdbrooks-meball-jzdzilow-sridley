FROM maven:latest
WORKDIR /back
RUN ["java", "-cp", "target/s0-1.0-SNAPSHOT.jar", "edu.brown.cs32.livecode.dispatcher.server.Server"]
