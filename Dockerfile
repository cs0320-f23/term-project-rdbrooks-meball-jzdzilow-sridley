FROM anapsix/alpine-java
CMD ["cd", "back"]
CMD ["java", "-cp", "s0-1.0-SNAPSHOT.jar", "edu.brown.cs32.livecode.dispatcher.server.Server"]
