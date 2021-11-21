package com.gs.pi4.api.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedActionException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public UnauthorizedActionException(String exception) {
		super(exception);
	}

	public UnauthorizedActionException(String exception, String message) {
		super(exception + " --> " + message);
	}
	
}
