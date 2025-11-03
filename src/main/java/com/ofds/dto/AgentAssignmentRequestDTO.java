package com.ofds.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentAssignmentRequestDTO {

    private Long orderId;
    private Long agentId;
}