package com.ofds.dto;

import lombok.Data;

@Data
public class PaymentOrderRequest {
    private Double amount; // Total amount in Rupees (from Angular form calculations)
    private Long customerId; 
    private String receipt; // A unique identifier for the order (e.g., customerId + timestamp)
}