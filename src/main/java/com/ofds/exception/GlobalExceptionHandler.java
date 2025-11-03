package com.ofds.exception;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.ofds.dto.DeliveryAgentDTO;

/**
 * This class is a Global Exception Handler for the entire application.
 * The @ControllerAdvice annotation allows it to handle exceptions across all controllers.
 * This way, we don't have to write try-catch blocks in every controller method.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * This method handles the DataNotFoundException.
     * When a DataNotFoundException is thrown anywhere in the application, this method will be called.
     * It returns a ResponseEntity with the exception message and an HTTP 404 (NOT_FOUND) status.
     */
    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<String> handleDataNotFoundException(DataNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * This method handles the RecordAlreadyFoundException.
     * It returns a ResponseEntity with the exception message and an HTTP 409 (CONFLICT) status.
     */
    @ExceptionHandler(RecordAlreadyFoundException.class)
    public ResponseEntity<String> handleRecordAlreadyFound(RecordAlreadyFoundException ex)
    {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }
    
    /**
     * This method handles the NoDataFoundException.
     * It returns a ResponseEntity with the exception message and an HTTP 404 (NOT_FOUND) status.
     */
    @ExceptionHandler(NoDataFoundException.class)
    public ResponseEntity<String> handleNoDataFound(NoDataFoundException ex) 
    {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * This is a general-purpose exception handler for any other exceptions that are not specifically handled above.
     * It logs the exception and returns a generic "Internal Server Error" message with an HTTP 500 status.
     * This prevents sensitive information from being leaked to the client.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        // It's a good practice to log the full exception for debugging purposes.
        // logger.error("An unexpected error occurred", ex);
        return new ResponseEntity<>("An unexpected error occurred. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(AgentListNotFoundException.class)
    public ResponseEntity<List<DeliveryAgentDTO>> handleAgentListNotFound(AgentListNotFoundException ex) {
        return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
    }

    @ExceptionHandler(AgentAssignmentException.class)
    public ResponseEntity<String> orderAssignmentException(AgentAssignmentException e) {
        String message = e.getMessage();
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<String> orderNotFoundException(OrderNotFoundException e) {
        String message = e.getMessage();
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MetricsDataNotFound.class)
    public ResponseEntity<String> metricsDataNotFound(MetricsDataNotFound e){
        String message = e.getMessage();
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }
}