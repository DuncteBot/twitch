FROM azul/zulu-openjdk-alpine:17 AS builder

WORKDIR /dunctebot
COPY . .
RUN ./gradlew --no-daemon build

FROM azul/zulu-openjdk-alpine:17-jre

WORKDIR /dunctebot
COPY --from=builder /dunctebot/build/libs/dunctebot-*-all.jar ./dunctebot.jar
CMD ["java", "-jar", "dunctebot.jar"]
