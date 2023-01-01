package org.observabilitystack.geoip;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.MoreObjects;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

/**
 * A geolocation database entry.
 */
@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, isGetterVisibility = NONE)
@JsonInclude(NON_EMPTY)
public class GeoIpEntry {

    private final String country;
    private final String stateprov;
    private final String stateprovCode;
    private final String city;
    private final String latitude;
    private final String longitude;
    private final String continent;
    private final String timezone;
    private final String isp;
    private final String organization;
    private final Long asn;
    private final String asnOrganization;

    private GeoIpEntry(String country, String stateprov, String stateprovCode, String city,
                       String latitude, String longitude, String continent, String timezone,
                       String isp, String organization, Long asn, String asnOrganization) {
        this.country = country;
        this.stateprov = stateprov;
        this.stateprovCode = stateprovCode;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.continent = continent;
        this.timezone = timezone;
        this.isp = isp;
        this.organization = organization;
        this.asn = asn;
        this.asnOrganization = asnOrganization;
    }

    public String getCountry() {
        return country;
    }

    public String getStateprov() {
        return stateprov;
    }

    public String getStateprovCode() {
        return stateprovCode;
    }

    public String getCity() {
        return city;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getContinent() {
        return continent;
    }

    public String getIsp() {
        return isp;
    }

    public String getOrganization() {
        return organization;
    }

    public Long getAsn() {
        return asn;
    }

    public String getAsnOrganization() {
        return asnOrganization;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("country", country)
                .add("stateprov", stateprov)
                .add("stateprovCode", stateprovCode)
                .add("city", city)
                .add("continent", continent)
                .add("latitude", latitude)
                .add("longitude", longitude)
                .add("timezone", timezone)
                .add("isp", isp)
                .add("organization", organization)
                .add("asn", asn)
                .add("asnOrganization", asnOrganization)
                .toString();
    }

    /**
     * Builder for {@link GeoIpEntry} instances.
     */
    public static class Builder {
        private String country;
        private String stateprov;
        private String stateprovCode;
        private String city;
        private String continent;
        private String latitude;
        private String longitude;
        private String timezone;
        private String isp;
        private String organization;
        private Long asn;
        private String asnOrganization;

        public Builder setCountry(String country) {
            this.country = country;
            return this;
        }

        public Builder setStateprov(String stateprov) {
            this.stateprov = stateprov;
            return this;
        }
        public Builder setStateprovCode(String stateprovCode) {
            this.stateprovCode = stateprovCode;
            return this;
        }

        public Builder setCity(String city) {
            this.city = city;
            return this;
        }
        public Builder setContinent(String continent) {
            this.continent = continent;
            return this;
        }

        public Builder setLatitude(String latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder setLongitude(String longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder setTimezone(String timezone) {
            this.timezone = timezone;
            return this;
        }

        public Builder setIsp(String isp) {
            this.isp = isp;
            return this;
        }

        public Builder setOrganization(String organization) {
            this.organization = organization;
            return this;
        }

        public Builder setAsn(Long asn) {
            this.asn = asn;
            return this;
        }

        public Builder setAsnOrganization(String asnOrganization) {
            this.asnOrganization = asnOrganization;
            return this;
        }

        public GeoIpEntry build() {
            return new GeoIpEntry(country, stateprov, stateprovCode, city, latitude, longitude, continent, timezone, isp, organization, asn,
                    asnOrganization);
        }
    }
}
