package com.ofds.exception;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.ofds.dto.DeliveryAgentDTO;


@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles exceptions where requested data (e.g., entity by ID) could not be found. Maps to HTTP 404 NOT FOUND.
     */
    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<String> handleDataNotFoundException(DataNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles exceptions where an attempt is made to create a record that already exists. Maps to HTTP 409 CONFLICT.
     */
    @ExceptionHandler(RecordAlreadyFoundException.class)
    public ResponseEntity<String> handleRecordAlreadyFound(RecordAlreadyFoundException ex)
    {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }
    
    /**
     * Handles exceptions where a query for a list returns no data. Maps to HTTP 404 NOT FOUND.
     */
    @ExceptionHandler(NoDataFoundException.class)
    public ResponseEntity<String> handleNoDataFound(NoDataFoundException ex) 
    {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Catches all unhandled exceptions as a fallback and maps them to HTTP 500 INTERNAL SERVER ERROR.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return new ResponseEntity<>("An unexpected error occurred. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * Handles cases where no delivery agents are found, returning an empty list with an HTTP 200 OK status.
     */
    @ExceptionHandler(AgentListNotFoundException.class)
    public ResponseEntity<List<DeliveryAgentDTO>> handleAgentListNotFound(AgentListNotFoundException ex) {
        return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
    }

    /**
     * Handles exceptions related to invalid order assignment attempts. Maps to HTTP 400 BAD REQUEST.
     */
    @ExceptionHandler(AgentAssignmentException.class)
    public ResponseEntity<String> orderAssignmentException(AgentAssignmentException e) {
        String message = e.getMessage();
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles exceptions when a specific order ID cannot be found. Maps to HTTP 404 NOT FOUND.
     */
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<String> orderNotFoundException(OrderNotFoundException e) {
        String message = e.getMessage();
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles exceptions when necessary dashboard metrics or aggregated data cannot be retrieved. Maps to HTTP 404 NOT FOUND.
     */
    @ExceptionHandler(MetricsDataNotFound.class)
    public ResponseEntity<String> metricsDataNotFound(MetricsDataNotFound e){
        String message = e.getMessage();
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Handles exceptions when a single specific agent cannot be found. Maps to HTTP 404 NOT FOUND with no body.
     */
    @ExceptionHandler(AgentNotFoundException.class)
    public ResponseEntity<Void> handleAgentNotFoundException(AgentNotFoundException ex) {
    	return new ResponseEntity<>(HttpStatus.NOT_FOUND);    
    }
}