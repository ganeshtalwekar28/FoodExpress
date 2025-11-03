package com.ofds.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Data Transfer Object (DTO) used to present a summary of an order in the order list view.
 * Fields are designed for direct consumption by the Angular front-end list model.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryDTO {

    // Unique ID of the order (matches entity ID)
    private Long id;
    // Redundant ID field for Angular compatibility
    private Long orderID;

    private String status;
    private String restaurantName;
    private String pickupAddress;

    private String customerName;
    private String dropAddress;

    // List of simplified items (OrderItemDTO) in the order
    private List<OrderItemDTO> items;

    // Count of unique line items in the order
    private Integer totalItems;

    private Double totalAmount;

    private LocalDateTime orderDate;

    private String agentName;
}