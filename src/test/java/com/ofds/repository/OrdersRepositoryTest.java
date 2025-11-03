package com.ofds.repository;

import com.ofds.entity.CustomerEntity;
import com.ofds.entity.DeliveryAgentEntity;
import com.ofds.entity.RestaurantEntity;
import com.ofds.entity.MenuItemEntity; // <-- Required for setup
import com.ofds.entity.OrderEntity;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = OnlineFoodDeliverySystemApplication.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) 
class OrdersRepositoryTest {

    @Autowired
    private OrderRepository ordersRepository;

    @Autowired
    private TestEntityManager entityManager; 

    private RestaurantEntity testRestaurant;
    private CustomerEntity testCustomer;
    private DeliveryAgentEntity testAgent;
    private OrderEntity pendingOrder;

    @BeforeEach
    @Transactional
    void setUp() {
        // === 1. Setup Base Entities (Requires setting all likely NOT NULL fields) ===
        
        testCustomer = new CustomerEntity();
        testCustomer.setName("Test Customer");
        testCustomer.setEmail("cust@test.com");
        testCustomer.setPassword("custpass");
        testCustomer.setPhone("1111111111");
        testCustomer = entityManager.persistAndFlush(testCustomer);

        testAgent = new DeliveryAgentEntity();
        testAgent.setName("Test Agent");
        testAgent.setEmail("agent@test.com");
        testAgent.setAgentCode("AG001");
        testAgent.setPassword("agentpass");
        testAgent.setPhone("2222222222");
        testAgent.setStatus("AVAILABLE");
        testAgent = entityManager.persistAndFlush(testAgent);

        // === 2. Setup Restaurant (Requires setting all likely NOT NULL fields) ===
        testRestaurant = new RestaurantEntity();
        testRestaurant.setName("Best Bistro");
        testRestaurant.setOwner_name("John Doe");
        testRestaurant.setEmail("bistro@test.com");
        testRestaurant.setPassword("restpass");
        testRestaurant.setPhone("3333333333");
        testRestaurant.setAddress("123 Test St");
        testRestaurant.setRating(4.8);
        testRestaurant = entityManager.persistAndFlush(testRestaurant);

        // === 3. Setup MenuItem (The CHILD table that caused the previous error) ===
        MenuItemEntity testMenuItem = new MenuItemEntity();
        testMenuItem.setName("Cheeseburger");
        testMenuItem.setPrice(12.50);
        
        // **CRITICAL FIX: Explicitly link the Menu Item to the Restaurant**
        testMenuItem.setRestaurant(testRestaurant); 
        entityManager.persistAndFlush(testMenuItem);

        // === 4. Setup the main OrdersEntity ===
        pendingOrder = new OrderEntity();
        pendingOrder.setUser(testCustomer);
        pendingOrder.setRestaurant(testRestaurant);
        pendingOrder.setAgent(testAgent);
        pendingOrder.setOrderStatus("PENDING");
        pendingOrder.setTotalAmount(15.00);
        pendingOrder.setDeliveryAddress("456 Main Ave");
        pendingOrder.setOrderDate(LocalDateTime.now());
        
        ordersRepository.save(pendingOrder); // Use the repository to test it
        entityManager.flush();
    }
    
 // ... (Make sure to import com.ofds.entity.AddressEntity)

    @AfterEach
    @Transactional
    void cleanUp() {
        // 1. DELETE THE DEEPEST CHILD TABLES (Resolves foreign key errors)

        // NEW FIX: Must delete AddressEntity before CustomerEntity
        entityManager.getEntityManager().createQuery("DELETE FROM AddressEntity").executeUpdate();
        
        // Previous fixes:
        entityManager.getEntityManager().createQuery("DELETE FROM CartItemEntity").executeUpdate(); 
        entityManager.getEntityManager().createQuery("DELETE FROM OrderItemsEntity").executeUpdate();
        
        // 2. DELETE PARENT TABLES
        entityManager.getEntityManager().createQuery("DELETE FROM CartEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM OrdersEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM MenuItemEntity").executeUpdate(); 
        
        // 3. DELETE GRANDPARENT TABLES (Now includes Customer at the end of this list)
        entityManager.getEntityManager().createQuery("DELETE FROM DeliveryAgentEntity").executeUpdate();
        
        // Now safe to delete CustomerEntity
        entityManager.getEntityManager().createQuery("DELETE FROM CustomerEntity").executeUpdate();
        
        entityManager.getEntityManager().createQuery("DELETE FROM RestaurantEntity").executeUpdate();
        
        entityManager.flush(); 
    }

    // =========================================================================
    //                            TEST PLACEHOLDERS
    // =========================================================================

    @Test
    void findById_ShouldReturnOrderDetails() {
        // ACT
        Optional<OrderEntity> foundOrder = ordersRepository.findById(pendingOrder.getId());
        
        // ASSERT
        assertTrue(foundOrder.isPresent(), "Order should be found by ID.");
        assertEquals("PENDING", foundOrder.get().getOrderStatus());
    }
    
    // Add other tests specific to your OrdersRepository methods here...
    // @Test
    // void findByStatus_ShouldReturnCorrectOrders() { ... }
}