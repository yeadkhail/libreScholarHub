FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8097
ENTRYPOINT ["java", "-jar", "app.jar"]