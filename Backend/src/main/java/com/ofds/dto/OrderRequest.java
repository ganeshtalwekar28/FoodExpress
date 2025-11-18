package com.ofds.dto;

import lombok.Data;

/**
 * DTO used by the client to send the final order data, including customer details, total amount, 
 * delivery address, and Razorpay payment confirmation.
 */
@Data
public class OrderRequest {
    private Long customerId;
    private Double totalAmount;
    
    private String deliveryAddress; 


    private String razorpayOrderId; 
    private String razorpayPaymentId;
    private String razorpaySignature;
}