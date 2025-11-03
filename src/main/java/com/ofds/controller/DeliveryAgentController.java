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

/**
 * REST controller for managing Delivery Agent information.
 * Handles operations to list all agents, available agents, and fetch detailed agent information.
 */
@RestController
@RequestMapping("/api/auth/admin/delivery-agents")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200") 
public class DeliveryAgentController {
	
    // Dependency injected via @RequiredArgsConstructor
    private final DeliveryAgentService deliveryAgentService;

    /**
     * Maps to GET /api/delivery-agents
     * Retrieves a list of ALL delivery agents (Available and Busy) with their current status and metrics.
     *
     * @return A ResponseEntity containing a list of DeliveryAgentDTOs with status 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<DeliveryAgentDTO>> listAllDeliveryAgents() throws AgentListNotFoundException {
        List<DeliveryAgentDTO> agents = deliveryAgentService.findAllDeliveryAgents();
        
        // FIX: Always return 200 OK. The service layer handles throwing exceptions 
        // if no data is found (and the test expects 200 OK on empty list anyway).
        return new ResponseEntity<>(agents, HttpStatus.OK);
     }

    /**
     * Maps to GET /api/delivery-agents/available
     * Retrieves a list of ONLY available delivery agents, typically for order assignment selection.
     *
     * @return A ResponseEntity containing a list of available DeliveryAgentDTOs with status 200 OK.
     */
    @GetMapping("/available")
    public ResponseEntity<List<DeliveryAgentDTO>> getAvailableDeliveryAgents() throws AgentListNotFoundException {
        List<DeliveryAgentDTO> agents = deliveryAgentService.findAvailableDeliveryAgents();
        
        // FIX: Always return 200 OK.
        return new ResponseEntity<>(agents, HttpStatus.OK);
    }

    /**
     * Maps to GET /api/delivery-agents/{agentId}
     * Retrieves detailed information for a single agent.
     * * @param agentId The ID of the agent to fetch.
     * @return A ResponseEntity containing the detailed DeliveryAgentDTO.
     * Note: AgentNotFoundException (404) is handled automatically by Spring due to its @ResponseStatus annotation.
     */
    @GetMapping("/{agentId}")
    public ResponseEntity<DeliveryAgentDTO> getAgentDetails(@PathVariable Long agentId) throws AgentNotFoundException {
        // The service method handles the lookup and throws AgentNotFoundException (or AgentListNotFoundException
        // as configured in the service) if not found.
        DeliveryAgentDTO details = deliveryAgentService.getAgentDetails(agentId);
        
        // This branch is only executed if details is successfully retrieved.
        return new ResponseEntity<>(details, HttpStatus.OK);
        
        // Note: The service layer throws the exception if the agent is not found, 
        // so we don't need an explicit 'else throw' block here.
    }
}


//package com.ofds.controller;
//
//import com.ofds.dto.DeliveryAgentDTO;
//import com.ofds.exception.AgentNotFoundException;
//import com.ofds.exception.AgentListNotFoundException;
//import com.ofds.service.DeliveryAgentService;
//
//import lombok.RequiredArgsConstructor;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
///**
// * REST controller for managing Delivery Agent information.
// * Handles operations to list all agents, available agents, and fetch detailed agent information.
// */
//@RestController
//@RequestMapping("/api/delivery-agents")
//@RequiredArgsConstructor
//public class DeliveryAgentController {
//	
//    // Dependency injected via @RequiredArgsConstructor
//    private final DeliveryAgentService deliveryAgentService;
//
//    /**
//     * Maps to GET /api/delivery-agents
//     * Retrieves a list of ALL delivery agents (Available and Busy) with their current status and metrics.
//     *
//     * @return A ResponseEntity containing a list of DeliveryAgentDTOs with status 200 OK.
//     */
//    @GetMapping
//    public ResponseEntity<List<DeliveryAgentDTO>> listAllDeliveryAgents() throws AgentListNotFoundException {
//        List<DeliveryAgentDTO> agents = deliveryAgentService.findAllDeliveryAgents();
//        if(!agents.isEmpty()) { 
//        	return new ResponseEntity<>(agents, HttpStatus.OK);
//        } else {
//        	// Throws exception, which GlobalExceptionHandler converts to 200 OK + [] body
//        	throw new AgentListNotFoundException("No delivery agents were found in the system.");
//        }
//     }
//
//    /**
//     * Maps to GET /api/delivery-agents/available
//     * Retrieves a list of ONLY available delivery agents, typically for order assignment selection.
//     *
//     * @return A ResponseEntity containing a list of available DeliveryAgentDTOs with status 200 OK.
//     */
//    @GetMapping("/available")
//    public ResponseEntity<List<DeliveryAgentDTO>> getAvailableDeliveryAgents() throws AgentListNotFoundException {
//        List<DeliveryAgentDTO> agents = deliveryAgentService.findAvailableDeliveryAgents();
//        if(!agents.isEmpty()) {
//        	return new ResponseEntity<>(agents, HttpStatus.OK);
//        } else {
//        	// Throws exception, which GlobalExceptionHandler converts to 200 OK + [] body
//        	throw new AgentListNotFoundException("No delivery agents are currently available.");
//        }
//    }
//
//    /**
//     * Maps to GET /api/delivery-agents/{agentId}
//     * Retrieves detailed information for a single agent.
//     * * @param agentId The ID of the agent to fetch.
//     * @return A ResponseEntity containing the detailed DeliveryAgentDTO.
//     * Note: AgentNotFoundException (404) is handled automatically by Spring due to its @ResponseStatus annotation.
//     */
//    @GetMapping("/{agentId}")
//    public ResponseEntity<DeliveryAgentDTO> getAgentDetails(@PathVariable Integer agentId) throws AgentNotFoundException {
//        DeliveryAgentDTO details = deliveryAgentService.getAgentDetails(agentId);
//        if(details != null) {
//            return new ResponseEntity<>(details, HttpStatus.OK);
//        } else {
//        	// Throws AgentNotFoundException, which typically maps to 404 Not Found
//        	throw new AgentNotFoundException("Delivery agent not found for ID: " + agentId);
//        }
//    }
//}