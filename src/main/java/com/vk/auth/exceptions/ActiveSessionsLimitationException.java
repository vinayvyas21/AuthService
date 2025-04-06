package com.vk.auth.exceptions;

public class ActiveSessionsLimitationException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ActiveSessionsLimitationException(String message) {
		super(message);
	}

}
