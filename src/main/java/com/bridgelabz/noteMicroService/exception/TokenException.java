package com.bridgelabz.noteMicroService.exception;

public class TokenException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	int errorCode;
	public TokenException(int errorCode, String msg) {
		super(msg);
		this.errorCode=errorCode;
	}
	public int getErrorCode() {
		return errorCode;
	}
}
