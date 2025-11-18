package com.ofds.repository;

import com.ofds.entity.CustomerEntity;
import com.ofds.entity.DeliveryAgentEntity;
import com.ofds.entity.OrderEntity;
import com.ofds.entity.RestaurantEntity;

import com.ofds.OnlineFoodDeliverySystemApplication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@ContextConfiguration(classes = OnlineFoodDeliverySystemApplication.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) 
class DeliveryAgentRepositoryTest {

    @Autowired
    private DeliveryAgentRepository agentRepository;

    @Autowired
    private TestEntityManager entityManager; 

    private DeliveryAgentEntity agentAvailable;
    private DeliveryAgentEntity agentBusy;
    private CustomerEntity mockCustomer;
    private RestaurantEntity mockRestaurant;

    @BeforeEach
    @Transactional
    void setUp() {
        
        entityManager.getEntityManager().createQuery("DELETE FROM OrderItemEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM CartItemEntity").executeUpdate(); 
        
        entityManager.getEntityManager().createQuery("DELETE FROM CartEntity").executeUpdate(); 
        entityManager.getEntityManager().createQuery("DELETE FROM MenuItemEntity").executeUpdate(); 
        
        entityManager.getEntityManager().createQuery("DELETE FROM OrderEntity").executeUpdate();
        
        entityManager.getEntityManager().createQuery("DELETE FROM DeliveryAgentEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM CustomerEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM RestaurantEntity").executeUpdate(); 
        
        entityManager.flush(); 
        
        mockCustomer = new CustomerEntity();
        mockCustomer.setName("TestCustomer");
        mockCustomer.setEmail("cust@test.com");
        mockCustomer.setPhone("9998887777");
        mockCustomer.setPassword("securepass123"); 
        mockCustomer = entityManager.persistAndFlush(mockCustomer);
        
        mockRestaurant = new RestaurantEntity();
        mockRestaurant.setName("TestRestaurant");
        mockRestaurant.setEmail("rest@test.com");
        mockRestaurant = entityManager.persistAndFlush(mockRestaurant);
        
        agentAvailable = new DeliveryAgentEntity();
        agentAvailable.setName("Agent One");
        agentAvailable.setEmail("agent1@test.com");
        agentAvailable.setAgentCode("A001");
        agentAvailable.setPassword("pass123");
        agentAvailable.setPhone("1112223333");
        agentAvailable.setStatus("AVAILABLE");
        agentAvailable.setTotalDeliveries(5);
        agentAvailable.setRating(4.5);
        agentAvailable = entityManager.persistAndFlush(agentAvailable);

        agentBusy = new DeliveryAgentEntity();
        agentBusy.setName("Agent Busy");
        agentBusy.setEmail("agent2@test.com");
        agentBusy.setAgentCode("A002");
        agentBusy.setPassword("pass456");
        agentBusy.setPhone("4445556666");
        agentBusy.setStatus("BUSY");
        agentBusy.setTotalDeliveries(10);
        agentBusy.setRating(4.0);
        agentBusy = entityManager.persistAndFlush(agentBusy);
        
        OrderEntity linkedOrder = new OrderEntity(); 
        linkedOrder.setTotalAmount(45.00); 
        linkedOrder.setOrderStatus("DELIVERED");
        linkedOrder.setAgent(agentBusy);
        linkedOrder.setUser(mockCustomer);
        linkedOrder.setRestaurant(mockRestaurant);
        linkedOrder.setOrderDate(LocalDateTime.now());
        linkedOrder = entityManager.persistAndFlush(linkedOrder); 

        agentBusy.getOrdersDelivered().add(linkedOrder); 
        entityManager.merge(agentBusy);

        entityManager.flush(); 
    }
    
    @AfterEach
    @Transactional
    void cleanUp() {

    }

    @Test
    void countByStatus_ShouldReturnCorrectCountForAvailable() {
        long count = agentRepository.countByStatus("AVAILABLE");
        
        assertEquals(1, count, "Should find exactly 1 agent with status AVAILABLE.");
    }
    
    @Test
    void countByStatus_ShouldReturnCorrectCountForBusy() {
        long count = agentRepository.countByStatus("BUSY");
        
        assertEquals(1, count, "Should find exactly 1 agent with status BUSY.");
    }
    
    @Test
    void countByStatus_ShouldReturnZeroForNonExistentStatus() {
        long count = agentRepository.countByStatus("OFFLINE");
        
        assertEquals(0, count, "Should find 0 agents with status OFFLINE.");
    }
    
    @Test
    void findByStatus_ShouldReturnCorrectList() {
        List<DeliveryAgentEntity> agents = agentRepository.findByStatus("BUSY");
        
        assertNotNull(agents);
        assertThat(agents, hasSize(1));
        assertEquals(agentBusy.getName(), agents.get(0).getName(), "The returned agent should be the busy agent.");
        assertEquals(agentBusy.getId(), agents.get(0).getId(), "The returned agent should be the busy agent.");

    }
    
    @Test
    void findByStatus_ShouldReturnEmptyListForNonExistentStatus() {
        List<DeliveryAgentEntity> agents = agentRepository.findByStatus("OFFLINE");
        
        assertNotNull(agents);
        assertTrue(agents.isEmpty(), "Should return an empty list for non-existent status.");
    }

    @Test
    void findAllWithOrdersEagerly_ShouldPreventNPlusOne() {
        List<DeliveryAgentEntity> agents = agentRepository.findAllWithOrdersEagerly();

        assertEquals(2, agents.size(), "Should return all agents.");

        DeliveryAgentEntity busyAgent = agents.stream()
            .filter(a -> a.getId().equals(agentBusy.getId()))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Busy agent not found in results."));
            
        assertEquals(1, busyAgent.getOrdersDelivered().size(), "Busy agent should have 1 order eagerly loaded.");
        
        DeliveryAgentEntity availableAgent = agents.stream()
            .filter(a -> a.getId().equals(agentAvailable.getId()))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Available agent not found in results."));
            
        assertTrue(availableAgent.getOrdersDelivered().isEmpty(), "Available agent should have 0 orders loaded.");
    }
    
    @Test
    void findByIdWithOrders_ShouldLoadAgentAndEagerlyFetchOrder() {
        Optional<DeliveryAgentEntity> result = agentRepository.findByIdWithOrders(agentBusy.getId());
        
        assertTrue(result.isPresent(), "Agent should be found.");
        
        DeliveryAgentEntity foundAgent = result.get();
        assertEquals(1, foundAgent.getOrdersDelivered().size(), "The order should be eagerly loaded.");
        assertEquals(agentBusy.getStatus(), foundAgent.getStatus(), "Should be the busy agent.");
    }
    
    @Test
    void findByIdWithOrders_ShouldReturnEmptyOptionalForNonExistentId() {
        Optional<DeliveryAgentEntity> result = agentRepository.findByIdWithOrders(999L);
        
        assertFalse(result.isPresent(), "Should return empty optional for non-existent ID.");
    }
}