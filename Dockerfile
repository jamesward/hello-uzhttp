FROM oracle/graalvm-ce:20.1.0-java11 as builder

WORKDIR /app
COPY . /app

RUN gu install native-image

RUN curl -L -o musl.tar.gz https://github.com/gradinac/musl-bundle-example/releases/download/v1.0/musl.tar.gz && \
    tar -xvzf musl.tar.gz

RUN ./sbt graalvm-native-image:packageBin

FROM scratch

COPY --from=builder /app/target/graalvm-native-image/hello-uzhttp /hello-uzhttp

ENTRYPOINT ["/hello-uzhttp"]
