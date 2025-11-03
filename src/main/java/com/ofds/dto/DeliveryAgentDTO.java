package com.ofds.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object (DTO) for conveying detailed information about a Delivery Agent
 * to the front-end, including metrics and current assignment status.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAgentDTO {

    // Core Agent Info
    private Long id; // Database Primary Key
    private String agentID; // Business ID/String Identifier
    private String name;
    private String phone;
    private String email;
    private String status; // e.g., "AVAILABLE", "BUSY"

    // Current Assignment
    private Long currentOrderID; // ID of the currently active order (null if available)

    // Performance Metrics
    private Double todayEarning;
    private Double totalEarning;
    private Integer totalDeliveries;
    private Double rating;

    // Orders List (Placeholder for a list of historical/summary orders, typically mapped to List<OrderSummaryDTO>)
    private List<Object> orders;
}