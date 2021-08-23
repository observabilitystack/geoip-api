# build stage
FROM maven:3.8.2-jdk-11-slim AS builder

ADD . /build
WORKDIR /build

# build image
RUN mvn --batch-mode --no-transfer-progress clean package -DskipTests=true  && \
    mv target/geoip-api-*.jar target/geoip-api.jar

# run stage
FROM adoptopenjdk:11-jre-hotspot-focal
ARG MAXMIND_LICENSE_KEY

# download current maxmind databases
WORKDIR /srv
#RUN apk add curl && \
RUN curl -sfSL "https://download.maxmind.com/app/geoip_download?edition_id=GeoLite2-City&suffix=tar.gz&license_key=${MAXMIND_LICENSE_KEY}" | tar -xz && \
    ln -s GeoLite2-City_*/GeoLite2-City.mmdb .

# place app
COPY --from=builder "/build/target/geoip-api.jar" /srv/geoip-api.jar

ENV CITY_DB_FILE /srv/GeoLite2-City.mmdb
HEALTHCHECK --interval=5s --timeout=1s CMD curl -f http://localhost:8080/actuator/health
EXPOSE 8080
CMD exec java -jar /srv/geoip-api.jar
