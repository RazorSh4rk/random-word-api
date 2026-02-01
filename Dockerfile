FROM buildo/scala-sbt-alpine:17.0.9_9_2.13.12_1.11.7 AS builder

WORKDIR /app

COPY build.sbt .
COPY project/ project/

# Download dependencies first (cached layer)
RUN sbt update

COPY src/ src/
COPY words.json .
COPY languages/ languages/
COPY templates/ templates/

# Build the application
RUN sbt stage

# Runtime image
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=builder /app/target/universal/stage/ ./
COPY --from=builder /app/words.json .
COPY --from=builder /app/languages/ languages/
COPY --from=builder /app/templates/ templates/

ENV PORT=9001
ENV HOST=0.0.0.0

EXPOSE 9001

CMD ["bin/random-word-api"]
