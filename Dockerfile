FROM openjdk:11-jdk-slim
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
COPY /src/main/resources/initial_data /initial_data
ENTRYPOINT ["java","-Dcrypto.initial.data=/initial_data", "-jar","/app.jar"]