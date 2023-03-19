package org.observabilitystack.geoip.web;

import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.observabilitystack.geoip.GeoIpEntry;
import org.springframework.http.HttpHeaders;

/**
 * Adds links to further information sources
 */
public class GeoIpEntryLinkHttpHeaders extends HttpHeaders {

    public GeoIpEntryLinkHttpHeaders(InetAddress address, GeoIpEntry entry) {
        super();

        final List<String> links = new LinkedList<>();
        links.add(String.format("<https://api.abuseipdb.com/api/v2/check?ipAddress=%s>; rel=\"abuse\"",
                address.getHostAddress()));
        links.add(String.format("<https://api.abuseipdb.com/api/v2/check?ipAddress=%s>; rel=\"abuse\"",
                address.getHostAddress()));

        if (entry.getAsn() != null) {
            links.add(String.format("<https://stat.ripe.net/data/as-overview/data.json?resource=%s>; rel=\"ripe-asn\"",
                    entry.getAsn()));
        }

        add(HttpHeaders.LINK, links.stream().collect(Collectors.joining(", ")));
    }

}
