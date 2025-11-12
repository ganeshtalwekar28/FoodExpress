package com.ofds.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an attempt is made to assign an agent to an order, but a business rule 
 * (like order status or agent availability) is violated.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AgentAssignmentException extends RuntimeException {

    private static final long serialVersionUID = 6802046766503603158L;

    public AgentAssignmentException(String message) {
        super(message);
    }
}