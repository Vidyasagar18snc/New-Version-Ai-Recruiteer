FROM openjdk:21-jdk-slim
WORKDIR /app
COPY build/libs/app.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
