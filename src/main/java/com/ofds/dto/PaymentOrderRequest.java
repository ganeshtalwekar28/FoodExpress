package com.ofds.dto;

import lombok.Data;

/**
 * DTO used to initiate a payment process by transferring the total amount, customer ID, and a unique receipt identifier to the server.
 */
@Data
public class PaymentOrderRequest {
    private Double amount;
    private Long customerId; 
    private String receipt;
}