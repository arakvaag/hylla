package org.rakvag.spotifyapi;

public class BadGatewayException extends RuntimeException {

	private static final long serialVersionUID = -5591231919391108069L;
	
	public BadGatewayException(String message) {
		super(message);
	}
	
	public BadGatewayException(String message, Throwable cause) {
		super(message, cause);
	}
}
