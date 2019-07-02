FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=inventory-0.0.1-SNAPSHOT.jar

# just copy and run the fat-jar file
COPY ./target/${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]