FROM ubuntu:latest AS BUILD
RUN apt-get update
RUN apt-get install openjdk-22-jdk -y

FROM openjdk:22-jdk-slim
EXPOSE 8080
COPY ./Backend/simpleonlinestore/simpleonlinestore-1.0.0.jar app.jar

ENTRYPOINT [ "java", "-jar", "app.jar" ]