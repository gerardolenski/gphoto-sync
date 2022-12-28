FROM maven:3.8.5-openjdk-17 AS build-stage
WORKDIR /app

COPY ./pom.xml ./pom.xml
RUN mvn dependency:go-offline

COPY ./lombok.config ./lombok.config
COPY ./src ./src
RUN mvn --batch-mode clean package


FROM openjdk:17-slim as run-stage
WORKDIR /app
COPY --from=build-stage /app/target/google-photo-synchronizer-*.jar /app/gphoto-sync.jar
ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar gphoto-sync.jar"]
