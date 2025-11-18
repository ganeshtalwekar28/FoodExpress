package com.ofds.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO used by the server to send confirmation details back to the client after a new order has been successfully placed.
 */
@Data
public class OrderResponse {
    private Long orderId; 
    private String status; 
    private Double totalAmount;
    private LocalDateTime orderDate;
    private String restaurantName;
    private List<OrderItemDTO> items; 
    private String razorpayOrderId;
    private LocalDateTime estimatedDelivery;
}