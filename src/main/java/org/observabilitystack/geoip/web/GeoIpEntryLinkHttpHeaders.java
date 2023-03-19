package org.observabilitystack.geoip.web;

import java.net.InetAddress;

import org.observabilitystack.geoip.GeoIpEntry;
import org.springframework.http.HttpHeaders;

/**
 * Adds links to further information sources
 */
public class GeoIpEntryLinkHttpHeaders extends HttpHeaders {

    public GeoIpEntryLinkHttpHeaders(InetAddress address, GeoIpEntry entry) {
        super();

        add(HttpHeaders.LINK, String.format("<https://api.abuseipdb.com/api/v2/check?ipAddress=%s>; rel=\"abuse\"",
                address.getHostAddress()));
        add(HttpHeaders.LINK, String.format(
                "<https://stat.ripe.net/data/reverse-dns-ip/data.json?resource=%s>; rel=\"ripe-reverse-dns\"",
                address.getHostAddress()));

        if (entry.getAsn() != null) {
            add(HttpHeaders.LINK,
                    String.format("<https://stat.ripe.net/data/as-overview/data.json?resource=%s>; rel=\"ripe-asn\"",
                            entry.getAsn()));
        }
    }

}
