FROM openjdk:17-ea-jdk-buster
WORKDIR /app/app/
COPY . /app/app/
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "target/datamock-0.0.1-SNAPSHOT.jar"]