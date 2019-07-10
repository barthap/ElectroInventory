# Dockerfile for LEGACY build

FROM openjdk:8-jre-alpine
VOLUME /tmp
ARG JAR_FILE

# just copy and run the fat-jar file
COPY ./target/${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]