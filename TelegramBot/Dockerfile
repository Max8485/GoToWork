FROM openjdk:17-alpine
COPY target/*.jar /app/TelegramBot.jar
ENTRYPOINT ["java", "-jar", "/app/TelegramBot.jar"]

#FROM maven:3.8.5-openjdk-17 as maven-build
#WORKDIR /app
#COPY . .
#RUN mvn clean package -pl TelegramBot -am -DskipTests
#
#FROM openjdk:17-jdk
#WORKDIR /app
#COPY --from=maven-build /app/TelegramBot/target/*.jar app.jar
#EXPOSE 8081
#ENTRYPOINT ["java", "-jar", "app.jar"]