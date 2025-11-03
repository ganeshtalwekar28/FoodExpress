package com.ofds.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception thrown when a Delivery Agent cannot be found by its ID.
 * Spring will automatically map this to an HTTP 404 Not Found response.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class AgentListNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -7409514133288930104L;

    public AgentListNotFoundException(String message) {
        super(message);
    }
}