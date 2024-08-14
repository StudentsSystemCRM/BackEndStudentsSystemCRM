FROM maven AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests


FROM eclipse-temurin:21-jre-alpine
WORKDIR /app1
COPY --from=build /app/target/edutreck_backend-0.0.1-SNAPSHOT.jar edutreck_backend.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "edutreck_backend.jar"]