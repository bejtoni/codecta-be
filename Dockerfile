# ---------- BUILD STAGE ----------
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache dependencies (brži re-build)
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests dependency:go-offline

# Kopiraj ostatak projekta i buildaj jar
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -DskipTests package

# ---------- RUNTIME STAGE ----------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# JVM tuning
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"
# Aktivni Spring profil
ENV SPRING_PROFILES_ACTIVE=prod

# Kopiraj buildani jar iz prve faze
COPY --from=build /app/target/*.jar app.jar

# Port iz application.properties
EXPOSE 5000

# Non-root user (security best practice)
USER 10001

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
