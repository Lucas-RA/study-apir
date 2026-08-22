FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /opt/app  
COPY . .
RUN mvn clean package -DskipTests

# segundo linux
FROM eclipse-temurin:21-alpine-3.21
WORKDIR /opt/app
COPY --from=build /opt/app/target/app.jar /opt/app/app.jar
# define valor padrão para a variável de ambiente SPRING_PROFILES_ACTIVE
ENV SPRING_PROFILES_ACTIVE=dev
CMD ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "app.jar"]

