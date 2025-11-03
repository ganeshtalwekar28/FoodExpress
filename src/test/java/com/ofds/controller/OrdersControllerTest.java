package com.ofds.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ofds.dto.AgentAssignmentRequestDTO;
import com.ofds.dto.DeliveryAgentDTO;
import com.ofds.entity.DeliveryAgentEntity;
import com.ofds.entity.OrderEntity;
import com.ofds.service.OrdersService;
import com.ofds.exception.AgentAssignmentException;
import com.ofds.exception.OrderNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


/**
 * Vertical Integration Test (WebMvcTest Slice) for OrdersController.
 * This tests the API endpoints, including the critical PUT requests for assignment and delivery,
 * with necessary security post-processors (user and csrf) to avoid 401/403 errors.
 */
@WebMvcTest(OrdersController.class)
class OrdersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @SuppressWarnings("removal")
	@MockBean
    private OrdersService ordersService;

    // --- Helper Entities ---
    private DeliveryAgentEntity mockAgent;
    private OrderEntity mockOrderOutForDelivery;
    private DeliveryAgentDTO availableAgentDTO;

    @BeforeEach
    void setUp() {
        // Mock Agent (Post-assignment status)
        mockAgent = new DeliveryAgentEntity();
        mockAgent.setId(201L);
        mockAgent.setName("Agent A");
        mockAgent.setStatus("BUSY");

        // Mock Order (Post-assignment state)
        mockOrderOutForDelivery = new OrderEntity();
        mockOrderOutForDelivery.setId(1001L);
        mockOrderOutForDelivery.setOrderStatus("OUT FOR DELIVERY");
        mockOrderOutForDelivery.setAgent(mockAgent);
        
        // Mock Available Agent DTO
        availableAgentDTO = new DeliveryAgentDTO(
                202L,                        // 1. id (Integer)
                "A002",                     // 2. agentID (String)
                "Agent B",                  // 3. name (String)
                "555-0101",                 // 4. phone (String) <-- Must be a String
                "agentb@ofds.com",          // 5. email (String) <-- Must be a String
                "AVAILABLE",                // 6. status (String)
                null,                       // 7. currentOrderID (Integer) <-- Must be null/Integer
                0.0,                        // 8. todayEarning (Double)
                0.0,                        // 9. totalEarning (Double)
                0,                          // 10. totalDeliveries (Integer) <-- Must be an Integer
                5.0,                        // 11. rating (Double) <-- Was missing
                Collections.emptyList()     // 12. orders (List<Object>) <-- Must be a List
            );
    }
    
    // =========================================================================
    // Test Cases for PUT /api/orders/assign (Agent Assignment)
    // =========================================================================

    @Test
    void assignAgentToOrder_ShouldReturnAgentNameAnd200OK_OnSuccess() throws Exception {
        // ARRANGE
        AgentAssignmentRequestDTO request = new AgentAssignmentRequestDTO(1001L, 201L);
        
        // Mock the service to return the updated order entity
        when(ordersService.assignAgent(request.getOrderId(), request.getAgentId()))
                .thenReturn(mockOrderOutForDelivery);

        // ACT & ASSERT
        mockMvc.perform(put("/api/orders/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(user("admin").roles("ADMIN")) // Authenticate as Admin
                .with(csrf())) // 👈 FIX: Include CSRF Token
                
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.agentName").value("Agent A"));
    }

    @Test
    void assignAgentToOrder_ShouldReturn404_WhenOrderNotFound() throws Exception {
        // ARRANGE
        AgentAssignmentRequestDTO request = new AgentAssignmentRequestDTO(9999L, 201L);
        
        // Mock the service to throw the expected exception
        when(ordersService.assignAgent(anyLong(), anyLong()))
                .thenThrow(new OrderNotFoundException("Order not found."));

        // ACT & ASSERT
        mockMvc.perform(put("/api/orders/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(user("admin").roles("ADMIN"))
                .with(csrf())) // 👈 FIX: Include CSRF Token
                
                .andExpect(status().isNotFound()); // Expect 404 (assuming OrderNotFoundException maps to 404)
    }

    @Test
    void assignAgentToOrder_ShouldReturn400_WhenOrderOrAgentStatusIsInvalid() throws Exception {
        // ARRANGE
        AgentAssignmentRequestDTO request = new AgentAssignmentRequestDTO(1001L, 201L);
        
        // Mock the service to throw the expected business rule exception
        when(ordersService.assignAgent(anyLong(), anyLong()))
                .thenThrow(new AgentAssignmentException("Order status not PLACED."));

        // ACT & ASSERT
        mockMvc.perform(put("/api/orders/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(user("admin").roles("ADMIN"))
                .with(csrf())) // 👈 FIX: Include CSRF Token
                
                .andExpect(status().isBadRequest()); // Expect 400 (assuming OrderAssignmentException maps to 400)
    }
    
    // =========================================================================
    // Test Cases for PUT /api/orders/{orderId}/deliver (Mark Delivered)
    // =========================================================================
    
    @Test
    void markOrderAsDelivered_ShouldReturnUpdatedStatusesAnd200OK() throws Exception {
        // ARRANGE
        OrderEntity deliveredOrder = new OrderEntity();
        deliveredOrder.setId(1001L);
        deliveredOrder.setOrderStatus("DELIVERED");
        
        DeliveryAgentEntity availableAgent = new DeliveryAgentEntity();
        availableAgent.setId(201L);
        availableAgent.setStatus("AVAILABLE");
        deliveredOrder.setAgent(availableAgent);
        
        // Request body for delivery
        String deliveryPayload = objectMapper.writeValueAsString(
            new HashMap<String, Object>() {
				private static final long serialVersionUID = 7933830945185562957L;
			{
                put("agentId", 201);
            }}
        );
        
        // Mock the service call
        when(ordersService.deliverOrder(1001L, 201L)).thenReturn(deliveredOrder);

        // ACT & ASSERT
        mockMvc.perform(put("/api/orders/{orderId}/deliver", 1001)
                .contentType(MediaType.APPLICATION_JSON)
                .content(deliveryPayload)
                .with(user("agent201").roles("DELIVERY")) // Authenticate as an Agent
                .with(csrf())) // 👈 FIX: Include CSRF Token
                
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("DELIVERED"))
                .andExpect(jsonPath("$.agentStatus").value("AVAILABLE"));
    }

    @Test
    void markOrderAsDelivered_ShouldReturn404_WhenOrderNotFound() throws Exception {
        // ARRANGE
        String deliveryPayload = objectMapper.writeValueAsString(
            new HashMap<String, Object>() {
				private static final long serialVersionUID = 6632483826004351622L;
			{
                put("agentId", 201);
            }}
        );
        
        // Mock the service to throw the expected exception
        when(ordersService.deliverOrder(9999L, 201L))
                .thenThrow(new OrderNotFoundException("Order not found."));

        // ACT & ASSERT
        mockMvc.perform(put("/api/orders/{orderId}/deliver", 9999)
                .contentType(MediaType.APPLICATION_JSON)
                .content(deliveryPayload)
                .with(user("agent201").roles("DELIVERY"))
                .with(csrf())) // 👈 FIX: Include CSRF Token
                
                .andExpect(status().isNotFound()); // Expect 404
    }
    
    // =========================================================================
    // Test Cases for GET /api/orders/agents/available
    // =========================================================================

    @Test
    void getAvailableDeliveryAgents_ShouldReturnAvailableAgents() throws Exception {
        // ARRANGE
        when(ordersService.findAvailableDeliveryAgents())
                .thenReturn(List.of(availableAgentDTO));

        // ACT & ASSERT
        mockMvc.perform(get("/api/orders/agents/available")
                .contentType(MediaType.APPLICATION_JSON)
                .with(user("admin").roles("ADMIN"))) // Authenticate (optional for GET, but safe)
                
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Agent B"))
                .andExpect(jsonPath("$[0].status").value("AVAILABLE"))
                .andExpect(jsonPath("$.length()").value(1));
    }
    
    // NOTE: For brevity, tests for other GET endpoints (getAllOrders, getOrderDetails) 
    // are omitted, but they follow the same pattern as the available agents test.
}