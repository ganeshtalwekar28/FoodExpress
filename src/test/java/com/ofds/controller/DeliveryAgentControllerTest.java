package com.ofds.controller;

import com.ofds.dto.DeliveryAgentDTO;
import com.ofds.service.DeliveryAgentService;
import com.ofds.exception.AgentListNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Vertical Integration Test (WebMvcTest Slice) for DeliveryAgentController.
 * This verifies the read-only GET endpoints for retrieving agent lists and details.
 */
@WebMvcTest(DeliveryAgentController.class)
class DeliveryAgentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
	@MockBean
    private DeliveryAgentService deliveryAgentService;

    // --- Helper DTOs ---
    private DeliveryAgentDTO availableAgent;
    private DeliveryAgentDTO busyAgent;

    @BeforeEach
    void setUp() {
        // Mock Agent DTOs (using the corrected 12-argument constructor structure)
        availableAgent = new DeliveryAgentDTO(
                101L, "A001", "Agent Alpha", "111-2222", "alpha@ofds.com", "AVAILABLE",
                null, 5.0, 100.0, 10, 4.8, Collections.emptyList());
        
        busyAgent = new DeliveryAgentDTO(
                102L, "A002", "Agent Beta", "333-4444", "beta@ofds.com", "BUSY",
                5001L, 10.0, 250.0, 25, 4.5, Collections.emptyList());
    }

    // =========================================================================
    // Test Cases for GET /api/delivery-agents (listAllDeliveryAgents)
    // =========================================================================

    @Test
    void listAllDeliveryAgents_ShouldReturnAllAgentsAnd200OK() throws Exception {
        // ARRANGE
        List<DeliveryAgentDTO> allAgents = List.of(availableAgent, busyAgent);
        when(deliveryAgentService.findAllDeliveryAgents()).thenReturn(allAgents);

        // ACT & ASSERT
        mockMvc.perform(get("/api/delivery-agents")
                .contentType(MediaType.APPLICATION_JSON)
                .with(user("admin").roles("ADMIN"))) // Authenticate as Admin
                
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Agent Alpha"))
                .andExpect(jsonPath("$[1].status").value("BUSY"));
    }

    @Test
    void listAllDeliveryAgents_ShouldReturnEmptyList_WhenNoAgentsExist() throws Exception {
        // ARRANGE
        when(deliveryAgentService.findAllDeliveryAgents()).thenReturn(Collections.emptyList());

        // ACT & ASSERT
        mockMvc.perform(get("/api/delivery-agents")
                .contentType(MediaType.APPLICATION_JSON)
                .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // =========================================================================
    // Test Cases for GET /api/delivery-agents/available (getAvailableDeliveryAgents)
    // =========================================================================

    @Test
    void getAvailableDeliveryAgents_ShouldReturnOnlyAvailableAgents() throws Exception {
        // ARRANGE
        List<DeliveryAgentDTO> availableAgents = List.of(availableAgent);
        when(deliveryAgentService.findAvailableDeliveryAgents()).thenReturn(availableAgents);

        // ACT & ASSERT
        mockMvc.perform(get("/api/auth/admin/delivery-agents/available")
                .contentType(MediaType.APPLICATION_JSON)
                .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("AVAILABLE"));
    }

    // =========================================================================
    // Test Cases for GET /api/delivery-agents/{agentId} (getAgentDetails)
    // =========================================================================

    @Test
    void getAgentDetails_ShouldReturnDetails_WhenAgentFound() throws Exception {
        // ARRANGE
        when(deliveryAgentService.getAgentDetails(102L)).thenReturn(busyAgent);

        // ACT & ASSERT
        mockMvc.perform(get("/api/auth/admin/delivery-agents/{agentId}", 102)
                .contentType(MediaType.APPLICATION_JSON)
                .with(user("admin").roles("ADMIN")))
                
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.agentID").value("A002"))
                .andExpect(jsonPath("$.currentOrderID").value(5001))
                .andExpect(jsonPath("$.totalDeliveries").value(25));
    }

    @Test
    void getAgentDetails_ShouldReturn404_WhenAgentNotFound() throws Exception {
        // ARRANGE
        when(deliveryAgentService.getAgentDetails(anyLong()))
                .thenThrow(new AgentListNotFoundException("Agent not found."));

        // ACT & ASSERT
        // Assuming AgentNotFoundException has an @ResponseStatus(HttpStatus.NOT_FOUND)
        mockMvc.perform(get("/api/auth/admin/delivery-agents/{agentId}", 999)
                .contentType(MediaType.APPLICATION_JSON)
                .with(user("admin").roles("ADMIN")))
                
                .andExpect(status().isNotFound()); // Expect 404
    }
}