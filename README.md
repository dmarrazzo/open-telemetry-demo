# Open Telemetry Demo

This project uses Quarkus, the Supersonic Subatomic Java Framework and is based on the online tutorial: https://quarkus.io/version/3.20/guides/opentelemetry.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

Run the 3 microservices in dev mode:

```sh
mvn quarkus:dev -f otel-front
mvn quarkus:dev -f greetings -Ddebug=5006
mvn quarkus:dev -f weather -Ddebug=5007
```

Call the front end service:

```sh
curl "http://localhost:8080/hello?name=John"
```

The following command provide information on the response time from client point of view:

```sh
curl -w "@curl-format.txt" -o /dev/null -s "http://localhost:8080/hello?name=John"
```

Open the [Dev UI](http://localhost:8080/q/dev-ui/extensions) and look for the **Obsevability** tile:

- Click on **Grafana UI** link.

## OpenShift Deployment

### Project

Login from command line: `oc login ...`

Create the project: `oc new-project otel`

### Install and configure LGTM stack

> [!WARNING]
> WORK IN PROGRESS

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
