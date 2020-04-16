FROM adoptopenjdk/openjdk8 as builder

WORKDIR /app
COPY .  /app

RUN ./sbt stage

FROM gcr.io/distroless/java:8

COPY --from=builder /app/target/universal/stage /app

CMD ["/app/bin/hello-uzhttp"]
