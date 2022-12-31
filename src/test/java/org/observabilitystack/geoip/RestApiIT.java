package org.observabilitystack.geoip;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.util.Map;

import com.google.common.net.InetAddresses;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.Country;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.observabilitystack.geoip.GeoIpApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = GeoIpApi.class)
@ActiveProfiles("test")
public class RestApiIT {

    private static final String REST_URL = "/{address}";

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean(name = "cityDatabaseReader")
    private DatabaseReader cityDatabaseReader;

    @MockBean(name = "ispDatabaseReader")
    private DatabaseReader ispDatabaseReader;

    @BeforeEach
    public void setUp() throws Exception {
        when(cityDatabaseReader.city(eq(InetAddresses.forString("192.0.2.1")))).thenReturn(
                new CityResponse(null, null,
                        new Country(null, 0, 0, "ZZ", null),
                        null, null, null, null, null, null, null));
        when(cityDatabaseReader.city(not(eq(InetAddresses.forString("192.0.2.1")))))
                .thenThrow(new AddressNotFoundException("test"));
        when(ispDatabaseReader.isp(any(InetAddress.class))).thenThrow(new AddressNotFoundException("test"));
    }

    @Test
    public void testRestApiRunningInContainer() {
        ResponseEntity<Map> response = restTemplate.getForEntity(REST_URL, Map.class, "192.0.2.1");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody().get("country")).isEqualTo("ZZ");
    }

    @Test
    public void test404ResponseForIpAddressWithoutEntry() {
        ResponseEntity<Map> response = restTemplate.getForEntity(REST_URL, Map.class, "192.0.2.2");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void test400ResponseForInvalidInput() {
        ResponseEntity<String> response = restTemplate.getForEntity(REST_URL, String.class, "invalid");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
