package com.ofds.dto;

import lombok.Data;

@Data
public class OrderRequest {
    private Integer customerId;
    private Double totalAmount; // Final amount including GST/Delivery
    
    // Address and Contact from Angular form
    private String deliveryAddress; 


    // Razorpay Details from the Angular handler response
    private String razorpayOrderId; 
    private String razorpayPaymentId;
    private String razorpaySignature;
}