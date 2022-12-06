FROM azul/zulu-openjdk-alpine:16 AS builder

WORKDIR /dunctebot
COPY gradle ./gradle
COPY gradlew build.gradle.kts settings.gradle.kts ./
RUN ./gradlew --no-daemon dependencies
COPY . .
RUN ./gradlew --no-daemon build

FROM azul/zulu-openjdk-alpine:16-jre

WORKDIR /dunctebot
COPY --from=builder /dunctebot/build/libs/dunctebot-*.jar ./dunctebot.jar
CMD ["java", "-jar", "dunctebot.jar"]
