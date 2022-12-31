package org.observabilitystack.geoip;

public class LookupException
    extends RuntimeException {

    public LookupException(String message, Throwable cause) {
        super(message, cause);
    }
}
