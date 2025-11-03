package com.ofds.dto;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Data Transfer Object (DTO) for conveying complete, detailed information for a single order.
 * It combines core order details, item list, customer/restaurant addresses, and a list of
 * available agents for assignment.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailsDTO {

    // --- Order Core Details ---
    private Long orderId;
    private String orderStatus;
    private Double totalAmount;

    // --- Customer Details ---
    private String customerName;
    private String customerAddress; // Used for drop address

    // --- Restaurant Details ---
    private String restaurantName;
    private String restaurantAddress; // Used for pickup address

    // --- Order Items ---
    private List<OrderItemDTO> items;

    // --- Agent Assignment Info ---
    private List<DeliveryAgentDTO> availableAgents;

    // Assigned agent details
    private String agentName;
}