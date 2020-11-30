# build stage
FROM oracle/graalvm-ce:20.2.0-java11 AS builder
 
ADD . /build
WORKDIR /build
 
# For SDKMAN to work we need unzip & zip
RUN yum -y install unzip zip
RUN \
    # Install SDKMAN
    curl -s "https://get.sdkman.io" | bash; \
    source "$HOME/.sdkman/bin/sdkman-init.sh" && \
    sdk install maven && \
    # Install GraalVM Native Image
    gu install native-image && \
    native-image --version && \
    mvn --version && \
    java -version && \
    ls -la .

# build image
RUN source "$HOME/.sdkman/bin/sdkman-init.sh" && mvn -P native clean package

# run stage
FROM centos:7.9.2009
ARG MAXMIND_LICENSE_KEY

# download current maxmind databases
WORKDIR /srv
#RUN apk add curl && \
RUN yum install -y tar gzip && \
    curl -sfSL "https://download.maxmind.com/app/geoip_download?edition_id=GeoLite2-City&suffix=tar.gz&license_key=${MAXMIND_LICENSE_KEY}" | tar -xz && \
    ln -s GeoLite2-City_*/GeoLite2-City.mmdb .

# place app
COPY --from=builder "/build/target/geoip-api" /srv/geoip-api

ENV CITY_DB_FILE /srv/GeoLite2-City.mmdb
HEALTHCHECK --interval=5s --timeout=1s CMD curl -f http://localhost:8080/actuator/health
EXPOSE 8080
CMD exec /srv/geoip-api
