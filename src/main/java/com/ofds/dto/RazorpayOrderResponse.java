package com.ofds.dto;

import lombok.Data;

/**
 * DTO used by the server to send the newly created Razorpay order details (ID, currency, amount) back to the client.
 */
@Data
public class RazorpayOrderResponse {
    private String orderId;
    private String currency;
    private Long amountInPaise;
}