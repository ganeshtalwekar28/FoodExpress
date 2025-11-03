package com.ofds.dto;

import lombok.Data;

@Data
public class RazorpayOrderResponse {
    private String orderId; // Need to change to Long as we are considering the id as a type of Long.
    private String currency;
    private Long amountInPaise;
}