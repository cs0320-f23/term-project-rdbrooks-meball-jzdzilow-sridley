FROM eclipse-temurin:17-jdk-alpine
WORKDIR /back
ENTRYPOINT ["java", "-cp", "target/s0-1.0-SNAPSHOT.jar", "edu.brown.cs32.livecode.dispatcher.server.Server"]
EXPOSE 3333
