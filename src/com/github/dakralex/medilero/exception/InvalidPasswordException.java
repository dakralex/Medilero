package com.github.dakralex.medilero.exception;

public class InvalidPasswordException extends Exception {
	public InvalidPasswordException () {
		super();
	}

	public InvalidPasswordException (String message) {
		super(message);
	}

	public InvalidPasswordException (String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidPasswordException (Throwable cause) {
		super(cause);
	}
}
