package com.ofds.exception;

public class RecordAlreadyFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5338484350427235988L;

	public RecordAlreadyFoundException(String msg)
	{
		super(msg);
	}
}

