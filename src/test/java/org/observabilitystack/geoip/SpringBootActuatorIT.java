package org.observabilitystack.geoip;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.net.InetAddress;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.micrometer.metrics.test.autoconfigure.AutoConfigureMetrics;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;

@AutoConfigureMetrics
@AutoConfigureTestRestTemplate
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = GeoIpApi.class)
@ActiveProfiles("test")
public class SpringBootActuatorIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoBean(name = "ispDatabaseReader")
    private DatabaseReader ispDatabaseReader;

    @BeforeEach
    public void setUp() throws Exception {
        when(ispDatabaseReader.isp(any(InetAddress.class))).thenThrow(new AddressNotFoundException("test"));
    }

    @Test
    public void testActuator() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator", String.class, "invalid");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void testActuatorHealth() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health", String.class, "invalid");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void testActuatorPrometheus() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/prometheus", String.class, "invalid");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
