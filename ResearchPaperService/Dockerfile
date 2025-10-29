FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar app.jar
COPY research-papers /app/research-papers
EXPOSE 8091
ENTRYPOINT ["java", "-jar", "app.jar"]