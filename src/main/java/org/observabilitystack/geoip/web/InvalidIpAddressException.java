package org.observabilitystack.geoip.web;

/**
 * Thrown if the REST API is called with an invalid IP address.
 */
public class InvalidIpAddressException extends RuntimeException {

    public InvalidIpAddressException(String message) {
        super(message);
    }
}
