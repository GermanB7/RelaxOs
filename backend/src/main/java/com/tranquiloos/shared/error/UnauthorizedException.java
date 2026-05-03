package com.tranquiloos.shared.error;

public class UnauthorizedException extends RuntimeException {

	public UnauthorizedException(String message) {
		super(message);
	}
}
