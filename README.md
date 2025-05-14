# Open Telemetry Demo

This project uses Quarkus, the Supersonic Subatomic Java Framework and is based on the online tutorial: https://quarkus.io/version/3.8/guides/opentelemetry.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

Run Jeager in your local machine:

```sh
podman run -d --rm --name jeager-aio \
    -p 16686:16686 -p 14268:14268 -p 4317:4317 -p 4318:4318 -p 14250:14250 \
    -e COLLECTOR_OTLP_ENABLED=true \
    jaegertracing/all-in-one:latest
```

> [!NOTE]
> If you have Docker in your machine, you can replace `podman` with `docker` in the previous command.

Run the 3 microservices in dev mode:

```sh
mvn quarkus:dev -f weather
mvn quarkus:dev -f greetings -Ddebug=5006
mvn quarkus:dev -f otel-front -Ddebug=5007
```

Call the front end service:

```sh
curl "http://localhost:8080/hello?name=John"
```

The following command provide information on the response time from client point of view:

```sh
curl -w "@curl-format.txt" -o /dev/null -s "http://localhost:8080/hello?name=John"
```

Open Jeager UI: [http://localhost:16686/]()

## OpenShift Deployment

### Project

Login from command line: `oc login ...`

Create the project: `oc new-project otel`

### Distributed tracing platform

1. In OperatorHub locate and **install**: `Red Hat OpenShift distributed tracing platform`

2. **Install** `Jeager all in one` service:

   ```
   oc apply -f k8s/jeager-all-in-one.yaml
   ```

3. Check that the status is running `oc get jaeger jaeger-all-in-one-inmemory -o jsonpath='{.status.phase}'`


> [!NOTE] 
> OpenShift is replacing Jeager with a more modern technology called Tempo. However, since Tempo does not yet provide an easy deployment for the development environment, we opted for a Jeager deployment that was sufficient to prove the key campabilities.

Further information can by found here: [Distributed tracing platform (Jaeger)](https://access.redhat.com/documentation/en-us/openshift_container_platform/4.15/html/distributed_tracing/distributed-tracing-platform-jaeger#doc-wrapper)

### Build images

Binary build of otel-weather app:

```sh
cd weather
mvn clean package
oc new-build --strategy docker --binary --name=otel-weather
ln -s src/main/docker/Dockerfile.jvm Dockerfile
oc start-build otel-weather --from-dir . --follow
oc patch imagestream otel-weather --type merge -p '{"spec":{"lookupPolicy":{"local":true}}}'
oc tag otel-weather:latest otel-weather:1.0
cd ..
```

Binary build of otel-greetings app:

```sh
cd greetings
mvn clean package
oc new-build --strategy docker --binary --name=otel-greetings
ln -s src/main/docker/Dockerfile.jvm Dockerfile
oc start-build otel-greetings --from-dir . --follow
oc patch imagestream otel-greetings --type merge -p '{"spec":{"lookupPolicy":{"local":true}}}'
oc tag otel-greetings:latest otel-greetings:1.0
cd ..
```

Binary build of otel-front app:

```sh
cd otel-front
mvn clean package
oc new-build --strategy docker --binary --name=otel-front
ln -s src/main/docker/Dockerfile.jvm Dockerfile
oc start-build otel-front --from-dir . --follow
oc patch imagestream otel-front --type merge -p '{"spec":{"lookupPolicy":{"local":true}}}'
oc tag otel-front:latest otel-front:1.0
cd ..
```

Remove completed build job:

```sh
oc delete pods --field-selector=status.phase=Succeeded
```

### Apply OpenShift manifest

Make sure that the OpenTelemetry collector endpoint match the configuration in [k8s/kustomization.yaml](), then apply the manifests:

```sh
oc apply -k k8s
```
