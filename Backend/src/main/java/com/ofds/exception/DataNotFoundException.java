package com.ofds.exception;

/**
 * Custom exception typically used when a requested entity (like a customer, restaurant, 
 * or cart) cannot be found in the database.
 */
public class DataNotFoundException extends Exception {
   
	private static final long serialVersionUID = 349509132763513335L;

	public DataNotFoundException(String message) {
        super(message);
    }
}