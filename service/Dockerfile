# Сборка (образы без alpine — мультиплатформа, в т.ч. Apple Silicon)
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src src

RUN ./gradlew installDist --no-daemon -q

# Рантайм
FROM eclipse-temurin:17-jre
WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends wget \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/build/install/service/ .

EXPOSE 8080
ENTRYPOINT ["./bin/service", "-port=8080"]
