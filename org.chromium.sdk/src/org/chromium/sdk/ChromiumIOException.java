package org.chromium.sdk;

import java.io.IOException;

public class ChromiumIOException extends IOException {
	private static final long serialVersionUID = 1L;

	private final Throwable cause;

	public ChromiumIOException(Throwable cause) {
		this.cause = cause;
	}
	
	public ChromiumIOException(String message, Throwable cause) {
		super(message);
		this.cause = cause;
	}
	
	@Override
	public Throwable getCause() {
		return cause;
	}
}
