FROM openjdk:17-alpine
COPY target/*.jar /app/GoToWorkCore.jar
ENTRYPOINT ["java", "-jar", "/app/GoToWorkCore.jar"]

#FROM maven:3.8.5-openjdk-17 AS build
#
#RUN mkdir -p /workspace
#WORKDIR /workspace
#COPY pom.xml /worcspace
#COPY src/ workspace/src
#
#RUN mvn -B -f pom.xml clean package -DskipTests -Dcheckstyle.skip
#
#FROM openjdk:17-alpine
#
#EXPOSE 8080
#COPY --from=build /workspace/target/*.jar app.jar
#ENTRYPOINT ["java", "-jar", "app.jar"]