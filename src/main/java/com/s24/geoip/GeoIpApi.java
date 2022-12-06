package com.s24.geoip;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;

//@TypeHint(typeNames = "org.springframework.context.annotation.ProfileCondition",
//    types={
//        GeoIpEntry.class, CityResponse.class, City.class, Continent.class,
//        Location.class, Postal.class, Country.class, RepresentedCountry.class,
//        Subdivision.class, Traits.class, NetworkDeserializer.class, Metadata.class,
//        AsnResponse.class, Network.class, IspResponse.class
//    })
@SpringBootApplication(proxyBeanMethods = false)
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
    @ConditionalOnProperty("CITY_DB_FILE")
    public DatabaseReader cityDatabaseReader(@Value("${CITY_DB_FILE}") String dbFileName) throws IOException {
        return buildDatabaseReader(dbFileName);
    }

    @Bean(name = "asnDatabaseReader")
    @ConditionalOnProperty("ASN_DB_FILE")
    public DatabaseReader asnDatabaseReader(@Value("${ASN_DB_FILE}") String dbFileName) throws IOException {
        return buildDatabaseReader(dbFileName);
    }

    @Bean(name = "ispDatabaseReader")
    @ConditionalOnProperty("ISP_DB_FILE")
    public DatabaseReader ispDatabaseReader(@Value("${ISP_DB_FILE}") String dbFileName) throws IOException {
        return buildDatabaseReader(dbFileName);
    }

    private DatabaseReader buildDatabaseReader(String fileName) throws IOException {
        File file = new File(fileName);
        DatabaseReader bean = new DatabaseReader.Builder(file).withCache(new CHMCache()).build();
        logger.info("Loaded database file {} (build date: {})", file, bean.getMetadata().getBuildDate());
        return bean;
    }
}
