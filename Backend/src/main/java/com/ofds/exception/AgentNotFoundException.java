package com.ofds.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a specific delivery agent requested by ID or criteria cannot be found, 
 * resulting in an HTTP 400 Bad Request or 404 Not Found response (depending on configuration).
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AgentNotFoundException extends Exception {

    private static final long serialVersionUID = -4903452964892993724L;

    public AgentNotFoundException(String message) {
        super(message);
    }
}