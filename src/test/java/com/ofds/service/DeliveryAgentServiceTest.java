package com.ofds.service;

import com.ofds.dto.DeliveryAgentDTO;
import com.ofds.entity.DeliveryAgentEntity;
import com.ofds.entity.OrderEntity;
import com.ofds.repository.DeliveryAgentRepository;
import com.ofds.repository.OrderRepository;
import com.ofds.exception.AgentListNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryAgentServiceTest {

    @Mock
    private DeliveryAgentRepository agentRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private DeliveryAgentService deliveryAgentService;


    private DeliveryAgentEntity availableAgent1;
    private DeliveryAgentEntity busyAgent2;
    private OrderEntity activeOrder;

    @BeforeEach
    void setUp() {
        // Setup a mock available agent
        availableAgent1 = new DeliveryAgentEntity();
        availableAgent1.setId(101L);
        availableAgent1.setName("Agent One");
        availableAgent1.setAgentCode("A001");
        availableAgent1.setStatus("AVAILABLE");
        availableAgent1.setTotalDeliveries(5);
        availableAgent1.setTotalEarnings(50.0);
        availableAgent1.setRating(4.5);
        availableAgent1.setOrdersDelivered(Collections.emptyList()); 

        // Setup a mock busy agent
        busyAgent2 = new DeliveryAgentEntity();
        busyAgent2.setId(102L);
        busyAgent2.setName("Agent Two");
        busyAgent2.setAgentCode("A002");
        busyAgent2.setStatus("BUSY");
        busyAgent2.setTotalDeliveries(10);
        busyAgent2.setTotalEarnings(100.0);
        busyAgent2.setRating(4.8);

        // Setup a mock active order entity for the busy agent (used in the mapAgentToDTO logic)
        activeOrder = new OrderEntity();
        activeOrder.setId(5001L);
        activeOrder.setOrderStatus("OUT FOR DELIVERY");
        busyAgent2.setOrdersDelivered(List.of(activeOrder));
    }

    @Test
    void findAvailableDeliveryAgents_ShouldReturnAvailableAgents() {
        // ARRANGE
        // Mock the repository to return a list of available agents
        when(agentRepository.findByStatus("AVAILABLE"))
                .thenReturn(List.of(availableAgent1));

        // ACT
        List<DeliveryAgentDTO> result = assertDoesNotThrow(() -> 
            deliveryAgentService.findAvailableDeliveryAgents());

        // ASSERT
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(availableAgent1.getName(), result.get(0).getName());
        assertEquals("AVAILABLE", result.get(0).getStatus());

        // Verify that the repository method was called exactly once
        verify(agentRepository, times(1)).findByStatus("AVAILABLE");
        verifyNoInteractions(orderRepository); // Should not check for active orders here
    }

    @Test
    void findAvailableDeliveryAgents_ShouldThrowException_WhenNoAgentsFound() {
        // ARRANGE
        // Mock the repository to return an empty list
        when(agentRepository.findByStatus("AVAILABLE"))
                .thenReturn(Collections.emptyList());

        // ACT & ASSERT
        AgentListNotFoundException thrown = assertThrows(AgentListNotFoundException.class, () -> 
            deliveryAgentService.findAvailableDeliveryAgents());

        // ASSERT
        assertEquals("Agent Data Not found in the Database...", thrown.getMessage());
        verify(agentRepository, times(1)).findByStatus("AVAILABLE");
    }

    @Test
    void getAgentDetails_ShouldReturnDetails_WhenAgentIsAvailable() {
        // ARRANGE
        // Mock finding the agent by ID
        when(agentRepository.findById(availableAgent1.getId()))
                .thenReturn(Optional.of(availableAgent1));
        // Mock the explicit check for active order (should find none)
        when(orderRepository.findActiveOrderByAgentId(availableAgent1.getId()))
                .thenReturn(Optional.empty());

        // ACT
        DeliveryAgentDTO result = assertDoesNotThrow(() -> 
            deliveryAgentService.getAgentDetails(availableAgent1.getId()));

        // ASSERT
        assertNotNull(result);
        assertEquals(availableAgent1.getName(), result.getName());
        assertEquals("AVAILABLE", result.getStatus());
        assertNull(result.getCurrentOrderID(), "Available agent should have no current order ID set from repo check.");

        verify(agentRepository, times(1)).findById(availableAgent1.getId());
        verify(orderRepository, times(1)).findActiveOrderByAgentId(availableAgent1.getId());
    }

    @Test
    void getAgentDetails_ShouldReturnDetailsAndActiveOrder_WhenAgentIsBusy() {
        // ARRANGE
        // Mock finding the busy agent by ID
        when(agentRepository.findById(busyAgent2.getId()))
                .thenReturn(Optional.of(busyAgent2));
        // Mock the explicit check for active order (should find one)
        when(orderRepository.findActiveOrderByAgentId(busyAgent2.getId()))
                .thenReturn(Optional.of(activeOrder));

        // ACT
        DeliveryAgentDTO result = assertDoesNotThrow(() -> 
            deliveryAgentService.getAgentDetails(busyAgent2.getId()));

        // ASSERT
        assertNotNull(result);
        assertEquals(busyAgent2.getName(), result.getName());
        assertEquals("BUSY", result.getStatus());
        // The service logic prioritizes the explicit check from OrdersRepository
        assertEquals(activeOrder.getId(), result.getCurrentOrderID(), 
            "Busy agent should have the correct current order ID from the repo check.");

        verify(agentRepository, times(1)).findById(busyAgent2.getId());
        verify(orderRepository, times(1)).findActiveOrderByAgentId(busyAgent2.getId());
    }

    @Test
    void getAgentDetails_ShouldThrowException_WhenAgentIdNotFound() {
        // ARRANGE
        Long nonExistentId = 999L;
        when(agentRepository.findById(nonExistentId))
                .thenReturn(Optional.empty());

        // ACT & ASSERT
        AgentListNotFoundException thrown = assertThrows(AgentListNotFoundException.class, () -> 
            deliveryAgentService.getAgentDetails(nonExistentId));

        // ASSERT
        assertEquals("Delivery Agent not found with ID: 999", thrown.getMessage());
        verify(agentRepository, times(1)).findById(nonExistentId);
        verifyNoInteractions(orderRepository);
    }
    
    @Test
    void findAllDeliveryAgents_ShouldReturnAllAgents_AndHandleDeduplicationAndActiveOrderCheck() {
        // ARRANGE
        OrderEntity activeOrder3 = new OrderEntity();
        activeOrder3.setId(5002L);
        activeOrder3.setOrderStatus("OUT FOR DELIVERY");
        
        DeliveryAgentEntity busyAgent3 = new DeliveryAgentEntity();
        busyAgent3.setId(103L);
        busyAgent3.setName("Agent Three");
        busyAgent3.setStatus("BUSY");
        busyAgent3.setOrdersDelivered(List.of(activeOrder3));
        
        List<DeliveryAgentEntity> rawRows = Arrays.asList(
            availableAgent1, 
            busyAgent2,     
            busyAgent3     
        );

        when(agentRepository.findAllWithOrdersEagerly()).thenReturn(rawRows);
        when(orderRepository.findActiveOrderByAgentId(101L)).thenReturn(Optional.empty());
        when(orderRepository.findActiveOrderByAgentId(102L)).thenReturn(Optional.of(activeOrder));
        when(orderRepository.findActiveOrderByAgentId(103L)).thenReturn(Optional.of(activeOrder3));


        // ACT
        List<DeliveryAgentDTO> result = assertDoesNotThrow(() -> 
            deliveryAgentService.findAllDeliveryAgents());

        // ASSERT
        assertNotNull(result);
        assertEquals(3, result.size()); 
        
        // Verify Agent 101 (Available)
        DeliveryAgentDTO dto1 = result.stream().filter(d -> d.getId().equals(101L)).findFirst().orElseThrow();
        assertEquals("AVAILABLE", dto1.getStatus());
        assertNull(dto1.getCurrentOrderID()); 

        // Verify Agent 102 (Busy) - The explicit repo check should win and set the OrderID
        DeliveryAgentDTO dto2 = result.stream().filter(d -> d.getId().equals(102L)).findFirst().orElseThrow();
        assertEquals("BUSY", dto2.getStatus());
        assertEquals(activeOrder.getId(), dto2.getCurrentOrderID(), "Current Order ID must come from the explicit repo check.");

        // Verify Agent 103 (Busy)
        DeliveryAgentDTO dto3 = result.stream().filter(d -> d.getId().equals(103L)).findFirst().orElseThrow();
        assertEquals("BUSY", dto3.getStatus());
        assertEquals(activeOrder3.getId(), dto3.getCurrentOrderID(), "Current Order ID must come from the explicit repo check.");

        verify(agentRepository, times(1)).findAllWithOrdersEagerly();
        // Verify that the explicit check was run for all 3 agents
        verify(orderRepository, times(1)).findActiveOrderByAgentId(101L);
        verify(orderRepository, times(1)).findActiveOrderByAgentId(102L);
        verify(orderRepository, times(1)).findActiveOrderByAgentId(103L);
    }
    
    @Test
    void findAllDeliveryAgents_ShouldThrowException_WhenNoAgentsFound() {
        // ARRANGE
        when(agentRepository.findAllWithOrdersEagerly())
                .thenReturn(Collections.emptyList());

        // ACT & ASSERT
        AgentListNotFoundException thrown = assertThrows(AgentListNotFoundException.class, () -> 
            deliveryAgentService.findAllDeliveryAgents());

        // ASSERT
        assertEquals("Delivery Agent cannot be found by its ID...", thrown.getMessage());
        verify(agentRepository, times(1)).findAllWithOrdersEagerly();
        verifyNoInteractions(orderRepository);
    }
}