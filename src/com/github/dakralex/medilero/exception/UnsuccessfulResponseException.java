package com.github.dakralex.medilero.exception;

public class UnsuccessfulResponseException extends Exception {
	public UnsuccessfulResponseException () {
		super();
	}

	public UnsuccessfulResponseException (String message) {
		super(message);
	}

	public UnsuccessfulResponseException (String message, Throwable cause) {
		super(message, cause);
	}

	public UnsuccessfulResponseException (Throwable cause) {
		super(cause);
	}
}
