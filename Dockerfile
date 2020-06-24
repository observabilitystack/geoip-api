FROM openjdk:11.0.7
ARG MAXMIND_LICENSE_KEY

# place app
COPY target/*.jar /opt/geoip-api.jar

# download current maxmind databases
WORKDIR /srv
RUN curl -sSL "https://download.maxmind.com/app/geoip_download?edition_id=GeoLite2-City&suffix=tar.gz&license_key=${MAXMIND_LICENSE_KEY}" | tar -xz && \
    ln -s GeoLite2-City_*/GeoLite2-City.mmdb .

ENV CITY_DB_FILE /srv/GeoLite2-City.mmdb
HEALTHCHECK CMD curl -f http://localhost:8080/actuator/health
EXPOSE 8080
CMD exec java ${JAVA_GC_OPTS} ${JAVA_OPTS} -jar /opt/geoip-api.jar
