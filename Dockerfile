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
# O Spring lê SPRING_PROFILES_ACTIVE diretamente do ambiente.
# O formato exec (JSON) não expande ${VAR} como um shell faria.
CMD ["java", "-jar", "app.jar"]

