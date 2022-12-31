package org.observabilitystack.geoip;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Objects;
import java.util.Optional;

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.AsnResponse;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.IspResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Continent;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Subdivision;

/**
 * Implements {@code GeolocationProvider} by using the Maxmind GeoIP database.
 */
public class MaxmindGeolocationDatabase
    implements GeolocationProvider {

    private final DatabaseReader cityDatabaseReader;
    private final DatabaseReader asnDatabaseReader;
    private final DatabaseReader ispDatabaseReader;

    public MaxmindGeolocationDatabase(DatabaseReader cityDatabaseReader, DatabaseReader asnDatabaseReader, DatabaseReader ispDatabaseReader) {
        if (cityDatabaseReader == null && ispDatabaseReader == null && asnDatabaseReader == null) {
            throw new IllegalArgumentException(
                "At least one of cityDatabaseReader, asnDatabaseReader, ispDatabaseReader must be non-null");
        }
        this.cityDatabaseReader = cityDatabaseReader;
        this.asnDatabaseReader = asnDatabaseReader;
        this.ispDatabaseReader = ispDatabaseReader;
    }

    @Override
    public Optional<GeoIpEntry> lookup(InetAddress addr) {
        GeoIpEntry.Builder responseBuilder = new GeoIpEntry.Builder();
        boolean hasCityData = lookupCityData(addr, responseBuilder);
        boolean hasAsnData = lookupAsnData(addr, responseBuilder);
        boolean hasIspData = lookupIspData(addr, responseBuilder);
        if (hasCityData || hasIspData || hasAsnData) {
            return Optional.of(responseBuilder.build());
        }
        return Optional.empty();
    }

    private boolean lookupCityData(InetAddress addr, GeoIpEntry.Builder builder) {
        if (cityDatabaseReader == null) {
            return false;
        }
        try {
            CityResponse response = cityDatabaseReader.city(addr);

            Optional.ofNullable(response.getCountry())
                    .map(Country::getIsoCode)
                    .ifPresent(builder::setCountry);
            Optional.ofNullable(response.getMostSpecificSubdivision())
                    .map(Subdivision::getName)
                    .ifPresent(builder::setStateprov);
            Optional.ofNullable(response.getMostSpecificSubdivision())
                    .map(Subdivision::getIsoCode)
                    .ifPresent(builder::setStateprovCode);
            Optional.ofNullable(response.getCity())
                    .map(City::getName)
                    .ifPresent(builder::setCity);
            Optional.ofNullable(response.getContinent())
                    .map(Continent::getCode)
                    .ifPresent(builder::setContinent);

            Optional.ofNullable(response.getLocation())
                    .ifPresent(
                        location -> {
                            Optional.ofNullable(location.getLatitude())
                                    .map(Objects::toString)
                                    .ifPresent(builder::setLatitude);
                            Optional.ofNullable(location.getLongitude())
                                    .map(Objects::toString)
                                    .ifPresent(builder::setLongitude);
                            Optional.ofNullable(location.getTimeZone())
                                    .map(Objects::toString)
                                    .ifPresent(builder::setTimezone);
                        }
                    );

            return true;

        } catch (AddressNotFoundException e) {
            // no city information found, this is not an error
            return false;
        } catch (IOException | GeoIp2Exception e) {
            throw new LookupException("Could not lookup city of address " + addr, e);
        }
    }

    private boolean lookupIspData(InetAddress addr, GeoIpEntry.Builder builder) {
        if (ispDatabaseReader == null) {
            return false;
        }
        try {
            IspResponse response = ispDatabaseReader.isp(addr);

            builder.setIsp(response.getIsp())
                   .setOrganization(response.getOrganization())
                   .setAsn(response.getAutonomousSystemNumber())
                   .setAsnOrganization(response.getAutonomousSystemOrganization());
            return true;

        } catch (AddressNotFoundException e) {
            // no ISP information found, this is not an error
            return false;
        } catch (IOException | GeoIp2Exception e) {
            throw new LookupException("Could not lookup ISP of address " + addr, e);
        }
    }

    private boolean lookupAsnData(InetAddress addr, GeoIpEntry.Builder builder) {
        if (asnDatabaseReader == null) {
            return false;
        }
        try {
            AsnResponse response = asnDatabaseReader.asn(addr);

            builder.setAsn(response.getAutonomousSystemNumber())
                   .setAsnOrganization(response.getAutonomousSystemOrganization());
            return true;

        } catch (AddressNotFoundException e) {
            // no ASN information found, this is not an error
            return false;
        } catch (IOException | GeoIp2Exception e) {
            throw new LookupException("Could not lookup ASN of address " + addr, e);
        }
    }
}
