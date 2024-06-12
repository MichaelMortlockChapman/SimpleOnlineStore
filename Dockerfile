FROM ubuntu:latest AS BUILD
RUN apt-get update
RUN apt-get install openjdk-22-jdk -y

ARG JWT_SECRET
ENV JWT_SECRET $JWT_SECRET
ARG DB_URL
ENV DB_URL $DB_URL
ARG DB_USERNAME
ENV DB_USERNAME $DB_USERNAME
ARG DB_PASSWORD
ENV DB_PASSWORD $DB_PASSWORD

FROM openjdk:22-jdk-slim
EXPOSE 8080
COPY ./Backend/simpleonlinestore/simpleonlinestore-1.0.0.jar app.jar

ENTRYPOINT [ "java", "-jar", "app.jar" ]