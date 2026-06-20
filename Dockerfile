# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -B package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre
WORKDIR /app
RUN mkdir -p /app/data
COPY --from=build /app/target/uno-cli.jar app.jar
VOLUME ["/app/data"]
ENTRYPOINT ["java", "-jar", "app.jar"]
CMD ["--bots", "3", "--games", "1", "--quiet", "--seed", "1"]
