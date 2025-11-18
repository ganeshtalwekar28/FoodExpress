package com.ofds.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for conveying essential metrics to the Admin Dashboard UI.
 */
@Data
@NoArgsConstructor 
@AllArgsConstructor 
public class AdminDashboardDTO {
	
	private long totalCustomers;
    private long totalRestaurants;
    private long totalDeliveryAgents;
    private long totalOrders;
    private long placedOrders;
    private long deliveredOrders;
    private long totalAvailableAgent;
    private long busyAgents;
    private Double totalRevenue; 
    
}