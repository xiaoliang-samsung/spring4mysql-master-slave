package org.cloudfoundry.samples.exception;

public class NoServiceException extends RuntimeException {
	private final String type;

	public NoServiceException(String type) {
		super("no service of this type: " + type);
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
