package org.observabilitystack.geoip;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.net.InetAddresses;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.AsnResponse;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.IspResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Continent;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Location;
import com.maxmind.geoip2.record.MaxMind;
import com.maxmind.geoip2.record.Postal;
import com.maxmind.geoip2.record.Subdivision;
import com.maxmind.geoip2.record.Traits;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.observabilitystack.geoip.GeoIpEntry;
import org.observabilitystack.geoip.MaxmindGeolocationDatabase;

public class MaxmindGeolocationDatabaseTest {

    private DatabaseReader cityDatabaseReader = Mockito.mock(DatabaseReader.class);
    private DatabaseReader asnDatabaseReader = Mockito.mock(DatabaseReader.class);
    private DatabaseReader ispDatabaseReader = Mockito.mock(DatabaseReader.class);
    private MaxmindGeolocationDatabase db = new MaxmindGeolocationDatabase(cityDatabaseReader, asnDatabaseReader, ispDatabaseReader);

    @BeforeEach
    public void setUp() throws Exception {
        List<String> locales = Collections.singletonList("DE");
        Country country = new Country(locales, 0, 0, "DE", ImmutableMap.of("DE", "Deutschland"));
        CityResponse cityResponse = new CityResponse(
                new City(locales, 0, 0, ImmutableMap.of("DE", "Hamburg")),
                new Continent(locales, "EU", 0, ImmutableMap.of("DE", "Europa")),
                country,
                new Location(0, 0, 53.5854, 10.0073, 0, 0, "Europe/Berlin"),
                new MaxMind(),
                new Postal("22301", 0),
                null,
                null,
                Lists.newArrayList(new Subdivision(locales, 0, 0, "DE", ImmutableMap.of("DE", "Hamburg"))),
                new Traits());
        IspResponse ispResponse = new IspResponse(64512, "private use range", "192.168.1.1", "local network", "foobar");
        AsnResponse asnResponse = new AsnResponse(64513, "private use range", "192.168.1.1");
        when(cityDatabaseReader.city(any(InetAddress.class))).thenReturn(cityResponse);
        when(asnDatabaseReader.asn(any(InetAddress.class))).thenReturn(asnResponse);
        when(ispDatabaseReader.isp(any(InetAddress.class))).thenReturn(ispResponse);
    }

    @Test
    public void testEmptyResponseIsConvertedCorrectly() throws Exception {
        CityResponse emptyCityResponse = new CityResponse(null, null, null, null, null, null, null, null, null, null);
        IspResponse emtpyIspResponse = new IspResponse(null, null, null, null, null);
        when(cityDatabaseReader.city(any(InetAddress.class))).thenReturn(emptyCityResponse);
        when(ispDatabaseReader.isp(any(InetAddress.class))).thenReturn(emtpyIspResponse);

        Optional<GeoIpEntry> result = db.lookup(InetAddresses.forString("192.168.1.1"));

        assertThat(result).isPresent();
        GeoIpEntry geoIpEntry = result.get();
        assertThat(geoIpEntry.getCountry()).isNull();
        assertThat(geoIpEntry.getStateprov()).isNull();
        assertThat(geoIpEntry.getStateprovCode()).isNull();
        assertThat(geoIpEntry.getCity()).isNull();
        assertThat(geoIpEntry.getContinent()).isNull();
        assertThat(geoIpEntry.getLatitude()).isNull();
        assertThat(geoIpEntry.getLongitude()).isNull();
        assertThat(geoIpEntry.getTimezone()).isNull();
        assertThat(geoIpEntry.getIsp()).isNull();
        assertThat(geoIpEntry.getOrganization()).isNull();
        assertThat(geoIpEntry.getAsn()).isNull();
        assertThat(geoIpEntry.getAsnOrganization()).isNull();
    }

    @Test
    public void testResponseWithDataIsConvertedCorrectly() {
        Optional<GeoIpEntry> result = db.lookup(InetAddresses.forString("192.168.1.1"));
        assertThat(result).isPresent();
        GeoIpEntry geoIpEntry = result.get();
        assertThat(geoIpEntry.getCountry()).isEqualTo("DE");
        assertThat(geoIpEntry.getStateprov()).isEqualTo("Hamburg");
        assertThat(geoIpEntry.getStateprovCode()).isEqualTo("DE");
        assertThat(geoIpEntry.getCity()).isEqualTo("Hamburg");
        assertThat(geoIpEntry.getContinent()).isEqualTo("EU");
        assertThat(geoIpEntry.getLatitude()).isEqualTo("53.5854");
        assertThat(geoIpEntry.getLongitude()).isEqualTo("10.0073");
        assertThat(geoIpEntry.getTimezone()).isEqualTo("Europe/Berlin");
        assertThat(geoIpEntry.getIsp()).isEqualTo("local network");
        assertThat(geoIpEntry.getOrganization()).isEqualTo("foobar");
        assertThat(geoIpEntry.getAsn()).isEqualTo(Integer.valueOf(64512));
        assertThat(geoIpEntry.getAsnOrganization()).isEqualTo("private use range");
    }

    @Test
    public void testDatabaseWithOnlyCityDatabase() {
        db = new MaxmindGeolocationDatabase(cityDatabaseReader, null, null);
        Optional<GeoIpEntry> result= db.lookup(InetAddresses.forString("192.168.1.1"));
        assertThat(result).isPresent();
        GeoIpEntry geoIpEntry = result.get();
        assertThat(geoIpEntry.getCountry()).isEqualTo("DE");
        assertThat(geoIpEntry.getStateprov()).isEqualTo("Hamburg");
        assertThat(geoIpEntry.getStateprovCode()).isEqualTo("DE");
        assertThat(geoIpEntry.getCity()).isEqualTo("Hamburg");
        assertThat(geoIpEntry.getContinent()).isEqualTo("EU");
        assertThat(geoIpEntry.getLatitude()).isEqualTo("53.5854");
        assertThat(geoIpEntry.getLongitude()).isEqualTo("10.0073");
        assertThat(geoIpEntry.getTimezone()).isEqualTo("Europe/Berlin");
        assertThat(geoIpEntry.getIsp()).isNull();
        assertThat(geoIpEntry.getOrganization()).isNull();
        assertThat(geoIpEntry.getAsn()).isNull();
        assertThat(geoIpEntry.getAsnOrganization()).isNull();
    }

    @Test
    public void testDatabaseWithOnlyIspDatabase() {
        db = new MaxmindGeolocationDatabase(null, null, ispDatabaseReader);
        Optional<GeoIpEntry> result = db.lookup(InetAddresses.forString("192.168.1.1"));
        assertThat(result).isPresent();
        GeoIpEntry geoIpEntry = result.get();
        assertThat(geoIpEntry.getCountry()).isNull();
        assertThat(geoIpEntry.getStateprov()).isNull();
        assertThat(geoIpEntry.getCity()).isNull();
        assertThat(geoIpEntry.getContinent()).isNull();
        assertThat(geoIpEntry.getLatitude()).isNull();
        assertThat(geoIpEntry.getLongitude()).isNull();
        assertThat(geoIpEntry.getTimezone()).isNull();
        assertThat(geoIpEntry.getIsp()).isEqualTo("local network");
        assertThat(geoIpEntry.getOrganization()).isEqualTo("foobar");
        assertThat(geoIpEntry.getAsn()).isEqualTo(Integer.valueOf(64512));
        assertThat(geoIpEntry.getAsnOrganization()).isEqualTo("private use range");
    }

    @Test
    public void testDatabaseWithOnlyAsnDatabase() {
        db = new MaxmindGeolocationDatabase(null, asnDatabaseReader, null);
        Optional<GeoIpEntry> result = db.lookup(InetAddresses.forString("192.168.1.1"));
        assertThat(result).isPresent();
        GeoIpEntry geoIpEntry = result.get();
        assertThat(geoIpEntry.getCountry()).isNull();
        assertThat(geoIpEntry.getStateprov()).isNull();
        assertThat(geoIpEntry.getCity()).isNull();
        assertThat(geoIpEntry.getContinent()).isNull();
        assertThat(geoIpEntry.getLatitude()).isNull();
        assertThat(geoIpEntry.getLongitude()).isNull();
        assertThat(geoIpEntry.getTimezone()).isNull();
        assertThat(geoIpEntry.getIsp()).isNull();
        assertThat(geoIpEntry.getOrganization()).isNull();
        assertThat(geoIpEntry.getAsn()).isEqualTo(Integer.valueOf(64513));
        assertThat(geoIpEntry.getAsnOrganization()).isEqualTo("private use range");
    }
}
