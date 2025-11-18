package com.ofds.controller;

import com.ofds.dto.DeliveryAgentDTO;
import com.ofds.exception.AgentNotFoundException;
import com.ofds.exception.AgentListNotFoundException;
import com.ofds.service.DeliveryAgentService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth/admin/delivery-agents")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200") 
public class DeliveryAgentController {
	
    private final DeliveryAgentService deliveryAgentService;

    /**
     * Retrieves a list of all registered delivery agents.
     */
    @GetMapping
    public ResponseEntity<List<DeliveryAgentDTO>> listAllDeliveryAgents() throws AgentListNotFoundException {
        List<DeliveryAgentDTO> agents = deliveryAgentService.findAllDeliveryAgents();
        
        return new ResponseEntity<>(agents, HttpStatus.OK);
     }

    /**
     * Retrieves a list of only the delivery agents who are currently available for new orders.
     */
    @GetMapping("/available")
    public ResponseEntity<List<DeliveryAgentDTO>> getAvailableDeliveryAgents() throws AgentListNotFoundException {
        List<DeliveryAgentDTO> agents = deliveryAgentService.findAvailableDeliveryAgents();
        
        return new ResponseEntity<>(agents, HttpStatus.OK);
    }

    /**
     * Retrieves the detailed information for a single delivery agent using their ID.
     */
    @GetMapping("/{agentId}")
    public ResponseEntity<DeliveryAgentDTO> getAgentDetails(@PathVariable Long agentId) throws AgentNotFoundException {
        DeliveryAgentDTO details = deliveryAgentService.getAgentDetails(agentId);
        
        return new ResponseEntity<>(details, HttpStatus.OK);
    }
}