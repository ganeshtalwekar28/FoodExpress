package com.ofds.dto;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO used to transfer the complete, detailed view of a specific order, including customer, restaurant,
 * item details, and the assigned agent's name.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailsDTO {

    private Long orderId;
    private String orderStatus;
    private Double totalAmount;

    private String customerName;
    private String customerAddress;

    private String restaurantName;
    private String restaurantAddress; 
    
    private List<OrderItemDTO> items;

    private List<DeliveryAgentDTO> availableAgents;
    private String agentName;
}