package com.ofds.exception;

/**
 * Custom exception typically thrown when an attempt is made to create a new entity 
 * (like a user or restaurant) that already exists in the database.
 */
public class RecordAlreadyFoundException extends RuntimeException {

	private static final long serialVersionUID = -5338484350427235988L;

	public RecordAlreadyFoundException(String msg) {
		super(msg);
	}
}