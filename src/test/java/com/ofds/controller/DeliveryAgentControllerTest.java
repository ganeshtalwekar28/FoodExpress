package com.ofds.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.ofds.config.JwtUtils;
import com.ofds.dto.DeliveryAgentDTO;
import com.ofds.exception.AgentNotFoundException;
import com.ofds.service.CustomerService;
import com.ofds.service.CustomerUserDetailsService;
import com.ofds.service.DeliveryAgentService;

@WebMvcTest(DeliveryAgentController.class)
class DeliveryAgentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private JwtUtils jwtUtils; 
    
    @MockBean
    private CustomerUserDetailsService customerUserDetailsService; 
    
    @MockBean
    private CustomerService customerService; 
    
    @SuppressWarnings("removal")
	@MockBean
    private DeliveryAgentService deliveryAgentService;

    // --- Helper DTOs ---
    private DeliveryAgentDTO availableAgent;
    private DeliveryAgentDTO busyAgent;
    
    private static final String BASE_URI = "/api/auth/admin/delivery-agents"; 

    @BeforeEach
    void setUp() {
        // Mock Agent DTOs 
        availableAgent = new DeliveryAgentDTO(
                101L, "A001", "Agent Alpha", "111-2222", "alpha@ofds.com", "AVAILABLE",
                null, 5.0, 100.0, 10, 4.8, Collections.emptyList());
        
        busyAgent = new DeliveryAgentDTO(
                102L, "A002", "Agent Beta", "333-4444", "beta@ofds.com", "BUSY",
                5001L, 10.0, 250.0, 25, 4.5, Collections.emptyList());

    }

    @Test
    void listAllDeliveryAgents_ShouldReturnAllAgentsAnd200OK() throws Exception {
        // ARRANGE
        List<DeliveryAgentDTO> allAgents = List.of(availableAgent, busyAgent);
        when(deliveryAgentService.findAllDeliveryAgents()).thenReturn(allAgents);

        // ACT & ASSERT
        mockMvc.perform(get(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .with(user("admin").roles("ADMIN"))) 
                
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void listAllDeliveryAgents_ShouldReturnEmptyList_WhenNoAgentsExist() throws Exception {
        // ARRANGE
        when(deliveryAgentService.findAllDeliveryAgents()).thenReturn(Collections.emptyList());

        // ACT & ASSERT
        mockMvc.perform(get(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAvailableDeliveryAgents_ShouldReturnOnlyAvailableAgents() throws Exception {
        // ARRANGE
        List<DeliveryAgentDTO> availableAgents = List.of(availableAgent);
        when(deliveryAgentService.findAvailableDeliveryAgents()).thenReturn(availableAgents);

        // ACT & ASSERT
        mockMvc.perform(get(BASE_URI + "/available")
                .contentType(MediaType.APPLICATION_JSON)
                .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("AVAILABLE"));
    }

    @Test
    void getAgentDetails_ShouldReturnDetails_WhenAgentFound() throws Exception {
        // ARRANGE
        when(deliveryAgentService.getAgentDetails(102L)).thenReturn(busyAgent);

        // ACT & ASSERT
        mockMvc.perform(get(BASE_URI + "/{agentId}", 102)
                .contentType(MediaType.APPLICATION_JSON)
                .with(user("admin").roles("ADMIN")))
                
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.agentID").value("A002"));
    }
    
    @Test
    void getAgentDetails_ShouldReturn404_WhenAgentNotFound() throws Exception {
        // ARRANGE
        when(deliveryAgentService.getAgentDetails(anyLong()))
                .thenThrow(new AgentNotFoundException("Agent not found.")); 

        // ACT & ASSERT
        mockMvc.perform(get(BASE_URI + "/{agentId}", 999)
                .contentType(MediaType.APPLICATION_JSON)
                .with(user("admin").roles("ADMIN")))
                
                .andExpect(status().isNotFound()); 
    }
}