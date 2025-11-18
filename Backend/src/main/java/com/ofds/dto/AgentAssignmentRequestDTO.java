package com.ofds.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO used to transfer data when assigning a specific delivery agent to an order.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentAssignmentRequestDTO {

    private Long orderId;
    private Long agentId;
    
}