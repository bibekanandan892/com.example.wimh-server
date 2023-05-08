FROM openjdk:11-jre-slim
EXPOSE 8080
WORKDIR /app
COPY build/libs/*.jar /app/wimh-server.jar
ENTRYPOINT ["java","-jar","/app/wimh-server.jar"]
