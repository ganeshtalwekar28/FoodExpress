package com.ofds.repository;

import com.ofds.entity.CustomerEntity;
import com.ofds.entity.DeliveryAgentEntity;
import com.ofds.entity.RestaurantEntity;
import com.ofds.entity.MenuItemEntity; 
import com.ofds.entity.OrderEntity;
import com.ofds.OnlineFoodDeliverySystemApplication;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.autoconfigure.domain.EntityScan; 
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = OnlineFoodDeliverySystemApplication.class)
@DataJpaTest
@EntityScan(basePackages = {"com.ofds.entity"}) 
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) 
class OrdersRepositoryTest {

    @Autowired
    private OrderRepository ordersRepository;

    @Autowired
    private TestEntityManager entityManager; 

    private RestaurantEntity testRestaurant;
    private CustomerEntity testCustomer;
    private DeliveryAgentEntity testAgent;
    private OrderEntity activeOrder;

    // --- ORDER STATUS CONSTANTS ---
    private static final String STATUS_PLACED = "PLACED";
    private static final String STATUS_DELIVERED = "DELIVERED";
    private static final String STATUS_OUT_FOR_DELIVERY = "OUT FOR DELIVERY";


    @BeforeEach
    @Transactional
    void setUp() {
      
        // Assuming these HQL names are correct for your entity classes:
        entityManager.getEntityManager().createQuery("DELETE FROM OrderItemEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM CartItemEntity").executeUpdate(); 
        entityManager.getEntityManager().createQuery("DELETE FROM CartEntity").executeUpdate();
        
        entityManager.getEntityManager().createQuery("DELETE FROM OrderEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM MenuItemEntity").executeUpdate(); 
        
        entityManager.getEntityManager().createQuery("DELETE FROM DeliveryAgentEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM CustomerEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM RestaurantEntity").executeUpdate();
        
        entityManager.flush(); 
        
        // === 1. Setup Base Entities ===
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

        // === 2. Setup Restaurant ===
        testRestaurant = new RestaurantEntity();
        testRestaurant.setName("Best Bistro");
        testRestaurant.setOwner_name("John Doe");
        testRestaurant.setEmail("bistro@test.com");
        testRestaurant.setPassword("restpass");
        testRestaurant.setPhone("3333333333");
        testRestaurant.setAddress("123 Test St"); 
        testRestaurant.setRating(4.8);
        testRestaurant = entityManager.persistAndFlush(testRestaurant);

        // === 3. Setup MenuItem ===
        MenuItemEntity testMenuItem = new MenuItemEntity();
        testMenuItem.setName("Cheeseburger");
        testMenuItem.setPrice(12.50);
        testMenuItem.setRestaurant(testRestaurant); 
        entityManager.persistAndFlush(testMenuItem);

        // === 4. Setup the main OrderEntity ===
        activeOrder = new OrderEntity();
        activeOrder.setUser(testCustomer);
        activeOrder.setRestaurant(testRestaurant);
        activeOrder.setAgent(testAgent);
        activeOrder.setOrderStatus(STATUS_PLACED); 
        activeOrder.setTotalAmount(15.00);
        activeOrder.setDeliveryAddress("456 Main Ave"); 
        activeOrder.setOrderDate(LocalDateTime.now());
        
        ordersRepository.save(activeOrder); 
        entityManager.flush();
    }
    
    @AfterEach
    @Transactional
    void cleanUp() {

    }

    @Test
    void findById_ShouldReturnOrderDetails() {
        // ACT
        Optional<OrderEntity> foundOrder = ordersRepository.findById(activeOrder.getId());
        
        // ASSERT
        assertTrue(foundOrder.isPresent(), "Order should be found by ID.");
        assertEquals(STATUS_PLACED, foundOrder.get().getOrderStatus()); 
        // ASSERTION to confirm delivery address is fetched from the OrderEntity
        assertEquals("456 Main Ave", foundOrder.get().getDeliveryAddress(), "Delivery address should be fetched from the OrderEntity.");
    }
    
    @Test
    void countByOrderStatus_ShouldReturnCorrectCount() {
        // ARRANGE: Persist another order with the same status
        OrderEntity anotherPlacedOrder = new OrderEntity();
        anotherPlacedOrder.setUser(testCustomer);
        anotherPlacedOrder.setRestaurant(testRestaurant);
        anotherPlacedOrder.setOrderStatus(STATUS_PLACED); 
        anotherPlacedOrder.setTotalAmount(5.00);
        anotherPlacedOrder.setDeliveryAddress("888 Other St");
        anotherPlacedOrder.setOrderDate(LocalDateTime.now().minusHours(1));
        ordersRepository.save(anotherPlacedOrder);
        entityManager.flush();

        // ACT
        long count = ordersRepository.countByOrderStatus(STATUS_PLACED); 

        // ASSERT
        assertEquals(2, count, "Should find 2 orders with status PLACED."); 
    }
    
    @Test
    void sumTotalAmountByStatusDelivered_ShouldCalculateCorrectTotal() {
        // ARRANGE: Persist DELIVERED orders
        OrderEntity deliveredOrder1 = new OrderEntity();
        deliveredOrder1.setUser(testCustomer);
        deliveredOrder1.setRestaurant(testRestaurant);
        deliveredOrder1.setOrderStatus(STATUS_DELIVERED); 
        deliveredOrder1.setTotalAmount(20.00);
        deliveredOrder1.setDeliveryAddress("789 Side St");
        deliveredOrder1.setOrderDate(LocalDateTime.now().minusDays(1));
        ordersRepository.save(deliveredOrder1);
        
        OrderEntity deliveredOrder2 = new OrderEntity();
        deliveredOrder2.setUser(testCustomer);
        deliveredOrder2.setRestaurant(testRestaurant);
        deliveredOrder2.setOrderStatus(STATUS_DELIVERED); 
        deliveredOrder2.setTotalAmount(30.00);
        deliveredOrder2.setDeliveryAddress("789 Side St");
        deliveredOrder2.setOrderDate(LocalDateTime.now().minusDays(2));
        ordersRepository.save(deliveredOrder2);
        
        entityManager.flush();

        // ACT
        Double totalRevenue = ordersRepository.sumTotalAmountByStatusDelivered();

        // ASSERT
        assertEquals(50.00, totalRevenue, 0.001, "The total delivered amount should be 50.00.");
    }
    
    @Test
    void updateOrderStatusById_ShouldChangeStatusAndReturnOne() {
        // ARRANGE
        Long orderId = activeOrder.getId();
        String newStatus = STATUS_OUT_FOR_DELIVERY; 

        // ACT
        int updatedCount = ordersRepository.updateOrderStatusById(orderId, newStatus);
        
        // Flush and Clear to ensure the next read hits the database
        entityManager.getEntityManager().flush();
        entityManager.getEntityManager().clear();

        // ASSERT
        assertEquals(1, updatedCount, "Exactly one row should be updated.");
        Optional<OrderEntity> updatedOrder = ordersRepository.findById(orderId);
        assertTrue(updatedOrder.isPresent());
        assertEquals(newStatus, updatedOrder.get().getOrderStatus(), "The status in the database should be updated to OUT FOR DELIVERY.");
    }

    @Test
    void findActiveOrderByAgentId_ShouldReturnOrderIfOutForDelivery() {
        // ARRANGE: Change the PLACED order to 'OUT FOR DELIVERY'
        activeOrder.setOrderStatus(STATUS_OUT_FOR_DELIVERY); 
        ordersRepository.save(activeOrder);
        entityManager.flush();

        // ACT
        Optional<OrderEntity> activeOrderFound = ordersRepository.findActiveOrderByAgentId(testAgent.getId());

        // ASSERT
        assertTrue(activeOrderFound.isPresent(), "Active order should be found for the agent.");
        assertEquals(activeOrder.getId(), activeOrderFound.get().getId());
        assertEquals(STATUS_OUT_FOR_DELIVERY, activeOrderFound.get().getOrderStatus());
    }
}