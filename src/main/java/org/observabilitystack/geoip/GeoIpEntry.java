package org.observabilitystack.geoip;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.MoreObjects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

/**
 * A geolocation database entry.
 */
@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, isGetterVisibility = NONE)
@JsonInclude(NON_EMPTY)
@lombok.Builder(setterPrefix = "set")
@ToString
@Getter
@AllArgsConstructor
public class GeoIpEntry {

    private final String country;
    private final String stateprov;
    private final String stateprovCode;
    private final String mobileCountryCode;
    private final String mobileNetworkCode;
    private final String city;
    private final String latitude;
    private final String longitude;
    private final String continent;
    private final String timezone;
    private final Integer usMetroCode;
    private final Integer accuracyRadius;
    private final Integer populationDensity;
    private final String isp;
    private final String organization;
    private final Long asn;
    private final String asnOrganization;
    private final String asnNetwork;

}
