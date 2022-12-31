package org.observabilitystack.geoip.web;

import java.util.Objects;

import org.observabilitystack.geoip.GeoIpEntry;
import org.springframework.http.HttpHeaders;

public class GeoIpEntryHttpHeaders extends HttpHeaders {

    public static final String X_GEOIP_ = "X-Geoip-";
    public static final String X_GEOIP_ADDRESS = X_GEOIP_ + "Address";
    public static final String X_GEOIP_COUNTRY = X_GEOIP_ + "Country";
    public static final String X_GEOIP_STATEPROV = X_GEOIP_ + "StateProv";
    public static final String X_GEOIP_CITY = X_GEOIP_ + "City";
    public static final String X_GEOIP_LATITUDE = X_GEOIP_ + "Latitude";
    public static final String X_GEOIP_LONGITUDE = X_GEOIP_ + "Longitude";
    public static final String X_GEOIP_CONTINENT = X_GEOIP_ + "Continent";
    public static final String X_GEOIP_TIMEZONE = X_GEOIP_ + "Timezone";
    public static final String X_GEOIP_ISP = X_GEOIP_ + "Isp";
    public static final String X_GEOIP_ORGANIZATION = X_GEOIP_ + "Organization";
    public static final String X_GEOIP_ASN = X_GEOIP_ + "Asn";
    public static final String X_GEOIP_ASN_ORGANIZATION = X_GEOIP_ + "AsnOrganization";

    public GeoIpEntryHttpHeaders(GeoIpEntry entry) {
        super();

        Objects.requireNonNull(entry);
        add(X_GEOIP_COUNTRY, entry.getCountry());
        add(X_GEOIP_STATEPROV, entry.getStateprov());
        add(X_GEOIP_CITY, entry.getCity());
        add(X_GEOIP_LATITUDE, entry.getLatitude());
        add(X_GEOIP_LONGITUDE, entry.getLongitude());
        add(X_GEOIP_CONTINENT, entry.getContinent());
        add(X_GEOIP_TIMEZONE, entry.getTimezone());
        add(X_GEOIP_ISP, entry.getIsp());
        add(X_GEOIP_ORGANIZATION, entry.getOrganization());

        if (entry.getAsn() != null) {
            add(X_GEOIP_ASN, String.valueOf(entry.getAsn()));
            add(X_GEOIP_ASN_ORGANIZATION, entry.getAsnOrganization());
        }
    }
}
