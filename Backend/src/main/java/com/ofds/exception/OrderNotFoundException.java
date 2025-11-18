package com.ofds.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a specific order cannot be located or retrieved using the provided ID or criteria, 
 * resulting in an HTTP 404 Not Found response.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class OrderNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -5153565148799490724L;

    public OrderNotFoundException(String message) {
        super(message);
    }
}