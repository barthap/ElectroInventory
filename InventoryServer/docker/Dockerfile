FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=inventory-0.2.0.jar

# just copy and run the fat-jar file
#COPY ./target/${JAR_FILE} app.jar
ADD ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]