package com.ofds.dto;

import com.ofds.entity.DeliveryAddress;
import lombok.Data;

@Data
public class FinalOrderRequest {
    private Long userId;
    private Long cartId;
    
    // Delivery Details (Snapshot from form)
    private DeliveryAddress deliveryAddress;

    // Razorpay Details (Required for server-side verification)
    private String razorpayPaymentId;
    private String razorpayOrderId;
    private String razorpaySignature;
    
    // Final calculated total (for cross-checking with server-side calculation)
    private Double finalAmount; 
}
