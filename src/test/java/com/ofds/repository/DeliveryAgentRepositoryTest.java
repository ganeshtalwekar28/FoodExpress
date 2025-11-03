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
        // --- CRITICAL PRE-TEST CLEANUP: Runs BEFORE every test to clear the database ---
        // Child tables must be deleted before their parent tables due to foreign key constraints.
        
        // 1. Delete lowest-level child tables
        entityManager.getEntityManager().createQuery("DELETE FROM OrderItemsEntity").executeUpdate();
        
        // 2. DELETE CART ITEM ENTITY (Child of Cart)
        // Assumes your Cart Item entity is named 'CartItemEntity'.
        entityManager.getEntityManager().createQuery("DELETE FROM CartItemEntity").executeUpdate(); 
        
        // 3. DELETE CART ENTITY (Child of Customer)
        // Assumes your Cart entity is named 'CartEntity'.
        entityManager.getEntityManager().createQuery("DELETE FROM CartEntity").executeUpdate(); 
        
        // 4. DELETE ADDRESS ENTITY (Child of Customer) 
        // Assumes your Address entity is named 'AddressEntity'.
        entityManager.getEntityManager().createQuery("DELETE FROM AddressEntity").executeUpdate(); 
        
        // 5. DELETE MENU ITEM ENTITY (New required step - Child of Restaurant) ⬅️ FIX FOR CURRENT ERROR
        // Assumes your Menu Item entity is named 'MenuItemEntity'.
        entityManager.getEntityManager().createQuery("DELETE FROM MenuItemEntity").executeUpdate(); 
        
        // 6. Delete from OrdersEntity (Child of DeliveryAgent, Customer, Restaurant)
        entityManager.getEntityManager().createQuery("DELETE FROM OrdersEntity").executeUpdate();
        
        // 7. Delete Parent Entities (Now safe to delete all parents)
        entityManager.getEntityManager().createQuery("DELETE FROM DeliveryAgentEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM CustomerEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM RestaurantEntity").executeUpdate(); // <<-- NOW SAFE
        
        entityManager.flush(); // Commit the cleanup before setup starts
        
        // === 1. Setup minimal dependencies for OrdersEntity ===
        mockCustomer = new CustomerEntity();
        mockCustomer.setName("TestCustomer");
        mockCustomer.setEmail("cust@test.com");
        mockCustomer.setPhone("9998887777");
        mockCustomer = entityManager.persistAndFlush(mockCustomer);
        
        mockRestaurant = new RestaurantEntity();
        mockRestaurant.setName("TestRestaurant");
        mockRestaurant.setEmail("rest@test.com");
        mockRestaurant = entityManager.persistAndFlush(mockRestaurant);
        
        // === 2. Setup Delivery Agents (including all NOT NULL fields) ===
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
        
        // === 3. Persist a linked Order ===
        OrderEntity linkedOrder = new OrderEntity(); 
        linkedOrder.setTotalAmount(45.00); 
        linkedOrder.setOrderStatus("DELIVERED");
        linkedOrder.setAgent(agentBusy);
        linkedOrder.setUser(mockCustomer);
        linkedOrder.setRestaurant(mockRestaurant);
        linkedOrder.setOrderDate(LocalDateTime.now());
        linkedOrder = entityManager.persistAndFlush(linkedOrder); 

        // Update the agent's list manually (best practice)
        agentBusy.getOrdersDelivered().add(linkedOrder); 
        entityManager.merge(agentBusy);

        entityManager.flush(); 
    }
    
    // --- AfterEach is now redundant but kept for completeness ---
    @AfterEach
    @Transactional
    void cleanUp() {
        // Cleanup is now handled effectively in @BeforeEach
    }

    // =========================================================================
    //                            TEST REPOSITORY METHODS
    // =========================================================================

    @Test
    void countByStatus_ShouldReturnCorrectCount() {
        // ACT
        long count = agentRepository.countByStatus("AVAILABLE");
        
        // ASSERT
        assertEquals(1, count, "Should find exactly 1 agent with status AVAILABLE.");
    }
    
    @Test
    void findByStatus_ShouldReturnCorrectList() {
        // ACT
        List<DeliveryAgentEntity> agents = agentRepository.findByStatus("BUSY");
        
        // ASSERT
        assertNotNull(agents);
        assertEquals(1, agents.size(), "Should find 1 agent with status BUSY.");
    }
    
    @Test
    void findAllWithOrdersEagerly_ShouldPreventNPlusOne() {
        // ACT
        List<DeliveryAgentEntity> agents = agentRepository.findAllWithOrdersEagerly();

        // ASSERT 1: Both agents are returned
        assertEquals(2, agents.size(), "Should return all agents.");

        // ASSERT 2: Find the agent with the order and check that the list is loaded
        DeliveryAgentEntity busyAgent = agents.stream()
            .filter(a -> a.getName().equals("Agent Busy"))
            .findFirst()
            .orElseThrow();
            
        assertEquals(1, busyAgent.getOrdersDelivered().size(), "Busy agent should have 1 order eagerly loaded.");
    }
    
    @Test
    void findByIdWithOrders_ShouldLoadAgentAndEagerlyFetchOrder() {
        // ACT
        Optional<DeliveryAgentEntity> result = agentRepository.findByIdWithOrders(agentBusy.getId());
        
        // ASSERT 1
        assertTrue(result.isPresent(), "Agent should be found.");
        
        // ASSERT 2: Check Eager Loading
        DeliveryAgentEntity foundAgent = result.get();
        assertEquals(1, foundAgent.getOrdersDelivered().size(), "The order should be eagerly loaded.");
    }
}