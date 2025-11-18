package com.ofds.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * DTO used to transfer detailed information and status updates about a delivery agent.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAgentDTO {

    private Long id; 
    private String agentID; 
    private String name;
    private String phone;
    private String email;
    private String status; 

    private Long currentOrderID; 

    private Double todayEarning;
    private Double totalEarning;
    private Integer totalDeliveries;
    private Double rating;

    private List<Object> orders;
}