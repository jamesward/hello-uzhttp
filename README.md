Hello uzhttp
------------

Run Locally:
```
./sbt ~reStart
```

TODO: Needs native-image installed or sbt build modification
Build the Native Image:
```
./sbt graalvm-native-image:packageBin
```

Run the Native Image:
```
target/graalvm-native-image/hello-uzhttp
```

Containerize Locally as a GraalVM native image:
```
docker build -t hello-uzhttp .
```

Run container:
```
docker run -p8080:8080 hello-uzhttp
```

Run on Cloud Run:

[![Run on Google Cloud](https://deploy.cloud.run/button.svg)](https://deploy.cloud.run)
