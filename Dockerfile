FROM maven AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app1
COPY --from=build /app/target/edutreck_backend-0.0.1-SNAPSHOT.jar edutreck_backend.jar
COPY ./opentelemetry-javaagent.jar .
COPY ./CustomTracesMetricsLogsExporter-0.0.1.jar .

EXPOSE 8080

ENTRYPOINT ["java", \
            "-javaagent:./opentelemetry-javaagent.jar", \
            "-Dotel.javaagent.extensions=./CustomTracesMetricsLogsExporter-0.0.1.jar", \
            "-Dotel.traces.exporter=none", \
            "-Dotel.metrics.exporter=none", \
            "-Dotel.logs.exporter=none", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-Dspans.destination.url=http://98.80.15.99:24224", \
            "-Dlogs.destination.url=http://98.80.15.99:24225", \
            "-Dmetrics.destination.url=http://98.80.15.99:24226", \
            "-Dmetric.interval.minutes=20", \
            "-jar", "edutreck_backend.jar"]