package com.ofds.exception;

/**
 * Custom exception typically thrown when a query is executed but returns an empty list or set of data, 
 * indicating no records matched the criteria.
 */
public class NoDataFoundException extends Exception{
	
	private static final long serialVersionUID = -7506687456108690405L;

	public NoDataFoundException(String msg) {
		super(msg);
	}
}