package com.ofds.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested list of delivery agents (e.g., all agents or available agents) 
 * cannot be found, resulting in an HTTP 404 Not Found response.
 */
@ResponseStatus(HttpStatus.NOT_FOUND) 
public class AgentListNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -1392191122838779026L;

	public AgentListNotFoundException(String message) {
        super(message);
    }
}