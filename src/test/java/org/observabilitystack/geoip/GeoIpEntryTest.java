package org.observabilitystack.geoip;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class GeoIpEntryTest {

    @Test
    public void testGetters() {
        GeoIpEntry entry = new GeoIpEntry.Builder()
                .setCountry("country")
                .setStateprov("region")
                .setCity("city")
                .setContinent("continent")
                .setLatitude("latitude")
                .setLongitude("longitude")
                .setTimezone("timezoneName")
                .setIsp("isp")
                .setOrganization("organization")
                .setAsn(64512l)
                .setAsnOrganization("asnOrganization")
                .build();

        assertThat(entry.getCountry()).isEqualTo("country");
        assertThat(entry.getStateprov()).isEqualTo("region");
        assertThat(entry.getCity()).isEqualTo("city");
        assertThat(entry.getContinent()).isEqualTo("continent");
        assertThat(entry.getLatitude()).isEqualTo("latitude");
        assertThat(entry.getLongitude()).isEqualTo("longitude");
        assertThat(entry.getTimezone()).isEqualTo("timezoneName");
        assertThat(entry.getIsp()).isEqualTo("isp");
        assertThat(entry.getOrganization()).isEqualTo("organization");
        assertThat(entry.getAsn()).isEqualTo(64512l);
        assertThat(entry.getAsnOrganization()).isEqualTo("asnOrganization");
    }
}
