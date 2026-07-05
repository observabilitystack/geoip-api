# AGENTS.md

Guidance for AI coding agents working in this repository.

## What this is

A Spring Boot 4.1.0 (Java 21, Maven) REST service that returns geolocation data for IP
addresses by querying MaxMind GeoIP2/GeoLite2 `.mmdb` databases. Distributed as a GraalVM
native-image Docker image, rebuilt weekly with fresh GeoLite2 data. Official maintained fork
of the original shopping24 project.

## Commands

Build & test (a plain `mvn` build produces a JAR; the production artifact is a native image built in Docker):

- `mvn clean verify` — full build, runs unit tests (`*Test`, surefire) **and** integration tests (`*IT`, failsafe). This is what CI runs.
- `mvn test` — unit tests only.
- `mvn test -Dtest=GeoIpEntryTest` — single unit test class; `-Dtest=GeoIpEntryTest#methodName` for one method.
- `mvn verify -Dit.test=RestApiIT` — single integration test.
- `mvn spring-boot:run` — run locally (see startup requirement below).

Docker (production build — needs a MaxMind license key to download the databases at build time):

```
export MAXMIND_LICENSE_KEY=...
docker build --build-arg MAXMIND_LICENSE_KEY=${MAXMIND_LICENSE_KEY} --build-arg VERSION=$(date +%Y-%V) -t geoip-api:latest .
docker run -p 8080:8080 geoip-api:latest
```

The multi-stage `Dockerfile` compiles the native image (`mvn native:compile -P native`) and
downloads GeoLite2-City/GeoLite2-ASN databases into the image. Note: README references a
`Dockerfile.native` for buildx multi-platform builds, but only `Dockerfile` exists (stale doc).

There is no linter/formatter config (no checkstyle/spotless/PMD/editorconfig) and no Maven
wrapper — use the system `mvn`.

## Startup requirement (important)

The app reads database file paths from **environment variables** at startup and will throw
`BeanInitializationException` ("Neither CITY_DB_FILE nor ASN_DB_FILE nor ISP_DB_FILE given")
if none is set. `.mmdb` files are **not** in the repo — obtain them from MaxMind for local runs:

| Env var | Purpose | Docker default |
|---|---|---|
| `CITY_DB_FILE` | GeoLite2/GeoIP2 City DB | `/srv/GeoLite2-City.mmdb` |
| `ASN_DB_FILE`  | GeoLite2/GeoIP2 ASN DB  | `/srv/GeoLite2-ASN.mmdb` |
| `ISP_DB_FILE`  | GeoIP2 ISP DB (commercial) | (none) |

Local run example: `CITY_DB_FILE=/path/GeoLite2-City.mmdb mvn spring-boot:run`. Service listens
on port 8080; health at `/actuator/health`, metrics at `/actuator/prometheus`.

## Architecture

Single base package `org.observabilitystack.geoip` (root = domain/config/lookup; `web` sub-package = HTTP layer).

Request flow: HTTP → `web/GeoIpRestController` → `GeolocationProvider` bean → MaxMind `DatabaseReader`(s) → merged into a `GeoIpEntry` → JSON body (or `X-Geoip-*` headers) + `Link` headers.

- **`GeoIpApi`** — `@SpringBootApplication` **and** the Spring `@Configuration`. Its `@Bean`
  methods build the three MaxMind `DatabaseReader`s from the env vars above (each optional, `null`
  if unset, wrapped with a `CHMCache`) and assemble the `GeolocationProvider`. Also carries
  `@ImportRuntimeHints(NativeImageConfiguration.class)` for the native build.
- **`GeolocationProvider`** (interface) / **`MaxmindGeolocationDatabase`** (impl) — per lookup calls
  `.city()`, `.asn()`, `.isp()` on whichever readers exist and merges into one `GeoIpEntry`.
  `AddressNotFoundException` = "no data" (not an error); other errors become `LookupException`.
- **`GeoIpEntry`** — Lombok `@Builder(setterPrefix="set")` DTO, Jackson `@JsonInclude(NON_EMPTY)`.
- **`web/GeoIpRestController`** — endpoints (no base path):
  - `GET /{address:.+}` → `GeoIpEntry` JSON + `Link` headers, or 404.
  - `GET /` with header `X-Geoip-Address` → 204, geo data returned as `X-Geoip-*` response headers.
  - `POST /` with a JSON array of IPs (max 100) → JSON map keyed by IP; unresolved IPs omitted.
  - `@InitBinder` registers `InetAdressPropertyEditor` to parse String→`InetAddress`.
- **`web/InetAdressPropertyEditor`** — parses IPs with Guava `InetAddresses.forString()` (strict,
  **no DNS resolution**). On failure it logs the raw parser message server-side and throws a
  **static** `"Invalid IP address given"` — do not echo user input back in the response body
  (this is the "Mitigate possible injection attack" fix in commit `3d5c2e7`; keep it that way).
- **`GeoIpApiMetricsExporter`** (`@Profile("!test")`) — Micrometer gauges `geoip.database` and
  `geoip.database_age_days` for alerting on stale databases.
- **`NativeImageConfiguration`** — GraalVM reflection hints for MaxMind model classes and `GeoIpEntry`.

Config: `application.yml` disables the banner and exposes only `health`+`prometheus` actuator
endpoints; logging via `log4j2.xml`.

## Tests

JUnit 5 + Mockito + AssertJ. `*Test` = unit (surefire), `*IT` = integration (failsafe,
`@SpringBootTest(RANDOM_PORT)`, `@ActiveProfiles("test")`, DB readers replaced with `@MockBean`).
`src/test/bats/smoke-test.sh` is a Bats end-to-end suite that runs in CI against a running
container image — not part of `mvn`.
