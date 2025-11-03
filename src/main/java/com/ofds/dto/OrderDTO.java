package com.ofds.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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
