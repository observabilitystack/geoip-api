package org.observabilitystack.geoip.web;

public class TooManyAddressesException extends RuntimeException {
	private static final long serialVersionUID = 742704466635106825L;

	public TooManyAddressesException(String message) {
        super(message);
    }
}
