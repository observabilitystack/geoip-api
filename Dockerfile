# ---------------------------------------------------------------------
# (1) build stage
# ---------------------------------------------------------------------
FROM observabilitystack/graalvm-maven-builder:ol9-java17-22.3.2 AS builder
ARG MAXMIND_LICENSE_KEY

ADD . /build
WORKDIR /build

# Build application
RUN mvn -B native:compile -P native --no-transfer-progress -DskipTests=true && \
    chmod +x /build/target/geoip-api

# Download recent geoip data
RUN curl -sfSL "https://download.maxmind.com/app/geoip_download?edition_id=GeoLite2-City&suffix=tar.gz&license_key=${MAXMIND_LICENSE_KEY}" | tar -xz && \
    curl -sfSL "https://download.maxmind.com/app/geoip_download?edition_id=GeoLite2-ASN&suffix=tar.gz&license_key=${MAXMIND_LICENSE_KEY}" | tar -xz && \
    mv GeoLite2-City_*/GeoLite2-City.mmdb . && \
    mv GeoLite2-ASN_*/GeoLite2-ASN.mmdb .

# ---------------------------------------------------------------------
# (2) run stage
# ---------------------------------------------------------------------
FROM debian:bookworm-slim
ARG CREATED_AT
ARG VERSION
ARG GIT_REVISION
#
## Add labels to identify release
LABEL org.opencontainers.image.authors="Torsten B. Köster <tbk@thiswayup.de>" \
      org.opencontainers.image.url="https://github.com/observabilitystack/geoip-api" \
      org.opencontainers.image.licenses="Apache-2.0" \
      org.opencontainers.image.title="Geoip-API" \
      org.opencontainers.image.description="A JSON REST API for Maxmind GeoIP databases" \
      org.opencontainers.image.created="${CREATED_AT}" \
      org.opencontainers.image.version="${VERSION}" \
      org.opencontainers.image.revision="${GIT_REVISION}"

## install curl for healthcheck
RUN apt-get update && \
    apt-get install -y curl && \
    apt-get clean

## place app and data
COPY --from=builder "/build/target/geoip-api" /srv/geoip-api
COPY --from=builder "/build/GeoLite2-City.mmdb" /srv/GeoLite2-City.mmdb
COPY --from=builder "/build/GeoLite2-ASN.mmdb" /srv/GeoLite2-ASN.mmdb

ENV CITY_DB_FILE /srv/GeoLite2-City.mmdb
ENV ASN_DB_FILE  /srv/GeoLite2-ASN.mmdb
HEALTHCHECK --interval=5s --timeout=1s CMD curl -f http://localhost:8080/actuator/health
EXPOSE 8080
CMD exec /srv/geoip-api
