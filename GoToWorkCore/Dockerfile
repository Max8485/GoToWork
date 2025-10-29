FROM openjdk:17-alpine
COPY target/*.jar /app/GoToWorkCore.jar
ENTRYPOINT ["java", "-jar", "/app/GoToWorkCore.jar"]

#FROM maven:3.8.5-openjdk-17 as maven-build
#WORKDIR /app
#COPY . .
#RUN mvn clean package -pl GoToWorkCore -am -DskipTests
#
#FROM openjdk:17-jdk
#WORKDIR /app
#COPY --from=maven-build /app/GoToWorkCore/target/*.jar app.jar
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "app.jar"]