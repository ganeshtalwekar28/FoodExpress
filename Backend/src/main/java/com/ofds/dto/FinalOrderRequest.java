package com.ofds.dto;

import com.ofds.entity.DeliveryAddress;
import lombok.Data;

/**
 * DTO used to gather all required information—user details, cart ID, payment details, and final amount—
 * necessary for placing and confirming a complete order.
 */
@Data
public class FinalOrderRequest {
    private Long userId;
    private Long cartId;
    
    private DeliveryAddress deliveryAddress;

    private String razorpayPaymentId;
    private String razorpayOrderId;
    private String razorpaySignature;
    
    private Double finalAmount; 
}