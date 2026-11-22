# Gateway Demo

A Spring Cloud Gateway demo built with Spring Boot and WebFlux. It demonstrates:

- Programmatic route configuration
- Request-header modification
- Host-based routing
- Resilience4j circuit-breaker fallback routing

## Requirements

- Java 17
- Maven

## Run the application

```bash
mvn spring-boot:run
```

The gateway starts on `http://localhost:8080` by default.

## Routes

### `/get`

Matches the `/get` path, adds `Hello: World` to the downstream request, and forwards the request to the configured HTTP endpoint.

```bash
curl -i http://localhost:8080/get
```

### Host-based circuit-breaker route

Requests with a host matching `*.circuitbreaker.com` are routed to the configured HTTP endpoint through a circuit breaker named `mycmd`. If the downstream request fails or times out, the gateway forwards to `/fallback`.

Example:

```bash
curl -i \
  -H "Host: www.circuitbreaker.com" \
  http://localhost:8080/delay/3
```

The fallback endpoint returns:

```text
fallback
```

Routes are evaluated in order. The gateway selects the first route whose predicates match.

## Configure the downstream endpoint

The default downstream URI is `http://httpbin.org:80`. Override it with a command-line property:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--httpbin=http://localhost:8081"
```

Or set it in `src/main/resources/application.yaml`:

```yaml
httpbin: http://localhost:8081
```

## Run tests

```bash
mvn test
```

The tests use WireMock and cover:

- Adding the `Hello` request header to `/get`
- Returning the local fallback for a delayed host-matched request
