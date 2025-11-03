package com.ofds.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for conveying essential metrics to the Admin Dashboard UI.
 * This class uses an all-arguments constructor for explicit object creation in the service layer.
 */
@Data
@NoArgsConstructor // Required for serialization/deserialization (e.g., JSON conversion)
@AllArgsConstructor // Used for explicit construction in the service layer
public class AdminDashboardDTO {

	private long totalCustomers;
    private long totalRestaurants;
    private long totalDeliveryAgents;
    private long totalOrders;
    private long placedOrders;
    private long deliveredOrders;
    private long totalAvailableAgent;
    private long busyAgents;
    private Double totalRevenue; // Sum of the total amounts of all delivered orders
}