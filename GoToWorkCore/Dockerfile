FROM maven:3.8.5-openjdk-17-slim AS build

WORKDIR /workspace
# 1. Копируем родительский POM и устанавливаем его
COPY pom.xml .
RUN mvn -B install -N -DskipTests

COPY CommonDto/pom.xml CommonDto/
COPY CommonDto/src CommonDto/src/
WORKDIR /workspace/CommonDto
RUN mvn -B clean install -DskipTests -Dcheckstyle.skip

WORKDIR /workspace
COPY GoToWorkCore/pom.xml GoToWorkCore/
COPY GoToWorkCore/src GoToWorkCore/src/
WORKDIR /workspace/GoToWorkCore
RUN mvn -B clean package -DskipTests -Dcheckstyle.skip

FROM amazoncorretto:17
WORKDIR /app
COPY --from=build /workspace/GoToWorkCore/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
