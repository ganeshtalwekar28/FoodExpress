package com.ofds.dto;

import lombok.Data;

@Data
public class RazorpayOrderResponse {
    private String orderId; 
    private String currency;
    private Long amountInPaise;
}