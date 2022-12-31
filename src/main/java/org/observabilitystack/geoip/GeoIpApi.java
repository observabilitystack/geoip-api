package org.observabilitystack.geoip;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;

import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;

@SpringBootApplication(proxyBeanMethods = false)
@ImportRuntimeHints(NativeImageConfiguration.class)
public class GeoIpApi {

    private static final Logger logger = LoggerFactory.getLogger(GeoIpApi.class);

    public static void main(String[] args) {
        SpringApplication.run(GeoIpApi.class, args);
    }

    @Bean
    public GeolocationProvider cityProvider(
            @Autowired(required = false) @Qualifier("cityDatabaseReader") DatabaseReader cityDatabaseReader,
            @Autowired(required = false) @Qualifier("asnDatabaseReader") DatabaseReader asnDatabaseReader,
            @Autowired(required = false) @Qualifier("ispDatabaseReader") DatabaseReader ispDatabaseReader) {
        if (cityDatabaseReader == null && ispDatabaseReader == null && asnDatabaseReader == null) {
            throw new BeanInitializationException("Neither CITY_DB_FILE nor ASN_DB_FILE nor ISP_DB_FILE given");
        }

        return new MaxmindGeolocationDatabase(cityDatabaseReader, asnDatabaseReader, ispDatabaseReader);
    }

    @Bean(name = "cityDatabaseReader")
    //@ConditionalOnProperty("CITY_DB_FILE")
    public DatabaseReader cityDatabaseReader() throws IOException {
        return buildDatabaseReaderFromEnvironment("CITY_DB_FILE");
    }

    @Bean(name = "asnDatabaseReader")
    //@ConditionalOnProperty("ASN_DB_FILE")
    public DatabaseReader asnDatabaseReader() throws IOException {
        return buildDatabaseReaderFromEnvironment("ASN_DB_FILE");
    }

    @Bean(name = "ispDatabaseReader")
    //@ConditionalOnProperty("ISP_DB_FILE")
    public DatabaseReader ispDatabaseReader() throws IOException {
        return buildDatabaseReaderFromEnvironment("ISP_DB_FILE");
    }

    private DatabaseReader buildDatabaseReaderFromEnvironment(String environment) throws IOException {
        Optional<String> filename = Optional.ofNullable(System.getenv(environment));

        if (filename.isPresent()) {
            return buildDatabaseReader(filename.get());
        }

        return null;
    }

    private DatabaseReader buildDatabaseReader(String fileName) throws IOException {
        File file = new File(fileName);
        DatabaseReader bean = new DatabaseReader.Builder(file).withCache(new CHMCache()).build();
        logger.info("Loaded database file {} (build date: {})", file, bean.getMetadata().getBuildDate());
        return bean;
    }
}
