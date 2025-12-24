FROM maven:3.8.5-openjdk-17-slim AS build

WORKDIR /workspace
# Копируем родительский POM сначала
COPY pom.xml .
RUN mvn -B install -N -DskipTests

COPY CommonDto/pom.xml CommonDto/
COPY CommonDto/src CommonDto/src/
WORKDIR /workspace/CommonDto
RUN mvn -B clean install -DskipTests -Dcheckstyle.skip

WORKDIR /workspace
COPY TelegramBot/pom.xml TelegramBot/
COPY TelegramBot/src TelegramBot/src/
WORKDIR /workspace/TelegramBot
RUN mvn -B -f pom.xml clean package -DskipTests -Dcheckstyle.skip

FROM amazoncorretto:17
WORKDIR /app
EXPOSE 8081
COPY --from=build /workspace/TelegramBot/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]


