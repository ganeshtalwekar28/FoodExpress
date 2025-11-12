package com.ofds.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when aggregated or summarized data (e.g., dashboard metrics) cannot be retrieved or calculated, 
 * resulting in an HTTP 404 Not Found response.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class MetricsDataNotFound extends Exception {

    private static final long serialVersionUID = -8266336014994621685L;

    public MetricsDataNotFound(String message) {
        super(message);
    }
}