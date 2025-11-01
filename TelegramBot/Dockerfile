FROM openjdk:17-alpine
COPY target/*.jar /app/TelegramBot.jar
ENTRYPOINT ["java", "-jar", "/app/TelegramBot.jar"]

#FROM maven:3.8.5-openjdk-17 AS build
#
#RUN mkdir -p /workspace
#WORKDIR /workspace
#COPY pom.xml /workspace
#COPY src /workspace/src
#
#RUN mvn -B -f pom.xml clean package -DskipTests -Dcheckstyle.skip
#
#FROM openjdk:17-alpine
#
#EXPOSE 8081
#COPY --from=build /workspace/target/*.jar app.jar
#ENTRYPOINT ["java", "-jar", "app.jar"]
