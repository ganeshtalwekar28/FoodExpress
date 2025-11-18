package com.ofds.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO used to present a summarized view of an order for display in lists on the admin or customer dashboards.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryDTO {

    private Long id;
    private Long orderID;

    private String status;
    private String restaurantName;
    private String pickupAddress;

    private String customerName;
    private String dropAddress;

    private List<OrderItemDTO> items;

    private Integer totalItems;
    private Double totalAmount;
    private LocalDateTime orderDate;
    private String agentName;
}