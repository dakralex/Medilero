package com.github.dakralex.medilero.exception;

public class NotMediolaException extends Exception {
	public NotMediolaException () {
		super();
	}

	public NotMediolaException (String message) {
		super(message);
	}

	public NotMediolaException (String message, Throwable cause) {
		super(message, cause);
	}

	public NotMediolaException (Throwable cause) {
		super(cause);
	}
}
