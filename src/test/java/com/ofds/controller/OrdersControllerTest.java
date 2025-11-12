package com.ofds.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ofds.config.JwtUtils; // Required import for security mock
import com.ofds.dto.AgentAssignmentRequestDTO;
import com.ofds.dto.DeliveryAgentDTO;
import com.ofds.entity.DeliveryAgentEntity;
import com.ofds.entity.OrderEntity;
import com.ofds.exception.AgentAssignmentException;
import com.ofds.exception.OrderNotFoundException;
import com.ofds.service.CustomerService; // Required import for security mock
import com.ofds.service.CustomerUserDetailsService; // Required import for security mock
import com.ofds.service.OrdersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration; // Import for exclusion
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(
    controllers = OrdersController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class} // Exclude security auto-config for clean tests
)
class OrdersControllerTest {

    private static final String BASE_PATH = "/api/auth/orders";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrdersService ordersService;

    @MockBean
    private JwtUtils jwtUtils;
    
    @MockBean
    private CustomerUserDetailsService customerUserDetailsService;
    
    @MockBean
    private CustomerService customerService;
    
    private DeliveryAgentEntity mockAgent;
    private OrderEntity mockOrderOutForDelivery;
    private DeliveryAgentDTO availableAgentDTO;
    private final Long MOCK_ORDER_ID = 1001L;
    private final Long MOCK_AGENT_ID = 201L;

    @BeforeEach
    void setUp() {
        mockAgent = new DeliveryAgentEntity();
        mockAgent.setId(MOCK_AGENT_ID);
        mockAgent.setName("Agent A");
        mockAgent.setStatus("BUSY");

        mockOrderOutForDelivery = new OrderEntity();
        mockOrderOutForDelivery.setId(MOCK_ORDER_ID);
        mockOrderOutForDelivery.setOrderStatus("OUT FOR DELIVERY");
        mockOrderOutForDelivery.setAgent(mockAgent);
        
        availableAgentDTO = new DeliveryAgentDTO(
                202L, 
                "A002", 
                "Agent B",                  
                "555-0101",                 
                "agentb@ofds.com",          
                "AVAILABLE",                
                null,                       
                0.0,                        
                0.0,                        
                0,                          
                5.0,                        
                Collections.emptyList()     
            );
    }
  
    @Test
    void assignAgentToOrder_ShouldReturnAgentNameAnd200OK_OnSuccess() throws Exception {
        // ARRANGE
        AgentAssignmentRequestDTO request = new AgentAssignmentRequestDTO(MOCK_ORDER_ID, MOCK_AGENT_ID);
        
        when(ordersService.assignAgent(eq(MOCK_ORDER_ID), eq(MOCK_AGENT_ID)))
                .thenReturn(mockOrderOutForDelivery);

        // ACT & ASSERT
        mockMvc.perform(put(BASE_PATH + "/admin/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(user("admin").roles("ADMIN")) 
                .with(csrf())) 
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.agentName").value("Agent A"));
    }

    @Test
    void assignAgentToOrder_ShouldReturn404_WhenOrderNotFound() throws Exception {
        // ARRANGE
        Long nonExistentId = 9999L;
        AgentAssignmentRequestDTO request = new AgentAssignmentRequestDTO(nonExistentId, MOCK_AGENT_ID);
        
        when(ordersService.assignAgent(eq(nonExistentId), anyLong()))
                .thenThrow(new OrderNotFoundException("Order details not found for ID : " + nonExistentId)); // Use OrderNotFoundException as per controller

        // ACT & ASSERT
        mockMvc.perform(put(BASE_PATH + "/admin/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(user("admin").roles("ADMIN"))
                .with(csrf()))
                .andExpect(status().isNotFound()); 
    }

    @Test
    void assignAgentToOrder_ShouldReturn400_WhenOrderOrAgentStatusIsInvalid() throws Exception {
        // ARRANGE
        AgentAssignmentRequestDTO request = new AgentAssignmentRequestDTO(MOCK_ORDER_ID, MOCK_AGENT_ID);
        String exceptionMessage = "Order status not PLACED or Agent not AVAILABLE.";
        
        when(ordersService.assignAgent(anyLong(), anyLong()))
                .thenThrow(new AgentAssignmentException(exceptionMessage));

        // ACT & ASSERT
        mockMvc.perform(put(BASE_PATH + "/admin/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(user("admin").roles("ADMIN"))
                .with(csrf())) 
                .andExpect(status().isBadRequest()) 
                .andExpect(content().string(exceptionMessage));
    }
    
    @Test
    void markOrderAsDelivered_ShouldReturnUpdatedStatusesAnd200OK() throws Exception {
        // ARRANGE
        OrderEntity deliveredOrder = new OrderEntity();
        deliveredOrder.setOrderStatus("DELIVERED");
        
        DeliveryAgentEntity availableAgent = new DeliveryAgentEntity();
        availableAgent.setStatus("AVAILABLE");
        deliveredOrder.setAgent(availableAgent);
        
        String deliveryPayload = objectMapper.writeValueAsString(
            new HashMap<String, Object>() {
				private static final long serialVersionUID = 7933830945185562957L; {
                put("agentId", MOCK_AGENT_ID.intValue()); // Use .intValue() to match JSON behavior
            }}
        );
        
        when(ordersService.deliverOrder(eq(MOCK_ORDER_ID), eq(MOCK_AGENT_ID))).thenReturn(deliveredOrder);

        // ACT & ASSERT
        mockMvc.perform(put(BASE_PATH + "/admin/{orderId}/deliver", MOCK_ORDER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(deliveryPayload)
                .with(user("admin").roles("ADMIN")) // Test using ADMIN role access
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("DELIVERED"))
                .andExpect(jsonPath("$.agentStatus").value("AVAILABLE"))
                .andExpect(jsonPath("$.currentOrderID").doesNotExist());
    }

    @Test
    void markOrderAsDelivered_ShouldReturn404_WhenOrderNotFound() throws Exception {
        // ARRANGE
        Long nonExistentId = 9999L;
        String deliveryPayload = objectMapper.writeValueAsString(
            new HashMap<String, Object>() {
				private static final long serialVersionUID = 6632483826004351622L;
			{
                put("agentId", MOCK_AGENT_ID.intValue());
            }}
        );
        String exceptionMessage = "Could not find or update Order ID 9999 for delivery.";
        when(ordersService.deliverOrder(eq(nonExistentId), anyLong())).thenReturn(null);

        // ACT & ASSERT
        mockMvc.perform(put(BASE_PATH + "/admin/{orderId}/deliver", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(deliveryPayload)
                .with(user("admin").roles("ADMIN"))
                .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().string(exceptionMessage));
    }
    
    @Test
    void getAvailableDeliveryAgents_ShouldReturn200OKWithAgents() throws Exception {
        // ARRANGE
        when(ordersService.findAvailableDeliveryAgents())
                .thenReturn(List.of(availableAgentDTO));

        // ACT & ASSERT
        mockMvc.perform(get(BASE_PATH + "/admin/agents/available")
                .contentType(MediaType.APPLICATION_JSON)
                .with(user("admin").roles("ADMIN"))) 
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Agent B"))
                .andExpect(jsonPath("$[0].status").value("AVAILABLE"))
                .andExpect(jsonPath("$.length()").value(1));
    }
    
}