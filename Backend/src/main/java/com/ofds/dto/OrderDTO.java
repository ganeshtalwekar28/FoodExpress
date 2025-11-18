package com.ofds.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO used for general transfer of order information, containing all core details, status,
 * payment method, and associated IDs for the customer, restaurant, and delivery agent.
 */
@Data
public class OrderDTO {
    private Long id;
    private LocalDateTime orderDate;
    private Double totalAmount;
    private String status;
    private String deliveryAddress;
    private Integer paymentMethod;
    private Integer customerId;
    private Integer restaurantId;
    private Integer agentId;
    
    private List<OrderItemDTO> items;
}