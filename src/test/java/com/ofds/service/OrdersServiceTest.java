package com.ofds.service;

import com.ofds.dto.CartDTO;
import com.ofds.dto.CartItemDTO;
import com.ofds.dto.DeliveryAgentDTO;
import com.ofds.dto.OrderDetailsDTO;
import com.ofds.dto.OrderRequest;
import com.ofds.dto.OrderResponse;
import com.ofds.entity.CustomerEntity;
import com.ofds.entity.DeliveryAgentEntity;
import com.ofds.entity.MenuItemEntity;
import com.ofds.entity.OrderEntity;
import com.ofds.entity.OrderItemEntity;
import com.ofds.entity.RestaurantEntity;
import com.ofds.exception.AgentAssignmentException;
import com.ofds.exception.AgentListNotFoundException;
import com.ofds.exception.DataNotFoundException;
import com.ofds.exception.OrderNotFoundException;
import com.ofds.mapper.OrderMapper;
import com.ofds.repository.CustomerRepository;
import com.ofds.repository.DeliveryAgentRepository;
import com.ofds.repository.MenuItemRepository;
import com.ofds.repository.OrderRepository;
import com.ofds.repository.OrdersItemsRepository;
import com.ofds.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdersServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrdersItemsRepository ordersItemsRepository;
    @Mock
    private CartService cartService;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private OrderMapper orderMapper;
    
    // This mock is needed because the service calls cartService.getMenuItemRepository()
    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private DeliveryAgentRepository agentRepository;

    // We mock the service that is a dependency to isolate the OrdersService logic
    @Mock
    private DeliveryAgentService deliveryAgentService;

    @InjectMocks
    private OrdersService ordersService;

    private OrderRequest orderRequest;
    private CartDTO cartDTO;
    private CustomerEntity customer;
    private RestaurantEntity restaurant;
    private MenuItemEntity menuItem;
    private OrderEntity savedOrder;
    private OrderResponse orderResponse;
    private OrderEntity placedOrder;
    private OrderEntity deliveredOrder;
    private DeliveryAgentEntity agentAvailable;
    private DeliveryAgentEntity agentBusy;
    private DeliveryAgentDTO availableAgentDTO;

    @BeforeEach
    void setUp() {
        // 1. Setup dummy data that will be returned by our mocks
        customer = new CustomerEntity();
        customer.setId(1L);
        customer.setName("Test User");

        restaurant = new RestaurantEntity();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");
        restaurant.setAddress("100 Main St");

        menuItem = new MenuItemEntity();
        menuItem.setId(101L);
        menuItem.setName("Test Dish");
        menuItem.setPrice(100.0);

        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setMenuItemId(101L);
        cartItemDTO.setName("Test Dish");
        cartItemDTO.setPrice(100.0);
        cartItemDTO.setQuantity(2);

        cartDTO = new CartDTO();
        cartDTO.setRestaurantId(1L);
        cartDTO.setItems(Collections.singletonList(cartItemDTO));

        orderRequest = new OrderRequest();
        orderRequest.setCustomerId(1L);
        orderRequest.setTotalAmount(236.0); // (100 * 2) + 18% tax
        orderRequest.setDeliveryAddress("123 Test St");
        orderRequest.setRazorpayOrderId("dummy_order_id");
        orderRequest.setRazorpayPaymentId("dummy_payment_id");
        orderRequest.setRazorpaySignature("dummy_signature");

        savedOrder = new OrderEntity();
        savedOrder.setId(1L);
        
        orderResponse = new OrderResponse();
        orderResponse.setOrderId(1L);
        orderResponse.setStatus("Placed");
        
        // ---------------------------------------------------------------------------------------------
        
        // Available Agent Entity & DTO
        agentAvailable = new DeliveryAgentEntity();
        agentAvailable.setId(201L);
        agentAvailable.setStatus("AVAILABLE");
        agentAvailable.setTotalDeliveries(0);
        agentAvailable.setTotalEarnings(0.0);
        
        availableAgentDTO = new DeliveryAgentDTO();
        availableAgentDTO.setId(201L);
        availableAgentDTO.setName("Agent A");

        // Busy Agent Entity
        agentBusy = new DeliveryAgentEntity();
        agentBusy.setId(202L);
        agentBusy.setStatus("BUSY");
        agentBusy.setTotalDeliveries(5);
        agentBusy.setTotalEarnings(50.0);
        agentBusy.setName("Agent B");
        
        // Order: PLACED (Ready for assignment)
        placedOrder = new OrderEntity();
        placedOrder.setId(1001L);
        placedOrder.setOrderStatus("PLACED");
        placedOrder.setTotalAmount(50.00);
        placedOrder.setUser(customer);
        placedOrder.setRestaurant(restaurant);
        placedOrder.setDeliveryAddress("456 Oak Ave");
        placedOrder.setOrderDate(LocalDateTime.now());
        placedOrder.setAgent(null);
        
     // Order: DELIVERED (For testing deliverOrder idempotency/edge cases)
        deliveredOrder = new OrderEntity();
        deliveredOrder.setId(1002L);
        deliveredOrder.setOrderStatus("DELIVERED");
        deliveredOrder.setTotalAmount(100.00);
        deliveredOrder.setAgent(agentBusy); // Agent that delivered it
        deliveredOrder.setUser(customer);
        deliveredOrder.setRestaurant(restaurant);
        deliveredOrder.setDeliveryAddress("789 Pine Ln");
        deliveredOrder.setOrderDate(LocalDateTime.now().minusDays(1));
    }

    @Test
    void placeOrder_Success() throws DataNotFoundException {
        // 2. Define the behavior of our mocks
        when(cartService.getCartByCustomerId(1L)).thenReturn(cartDTO);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        
        // This is the tricky part that was likely failing before
//        when(cartService.getMenuItemRepository()).thenReturn(menuItemRepository);
        when(menuItemRepository.findById(101L)).thenReturn(Optional.of(menuItem));
        
        // When the service tries to save the order, return our dummy savedOrder
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(savedOrder);
        
        // When the service tries to map the entity to a response, return our dummy response
        when(orderMapper.toResponse(any(OrderEntity.class))).thenReturn(orderResponse);

        // 3. Call the actual method we want to test
        OrderResponse result = ordersService.placeOrder(orderRequest);

        // 4. Assert and verify the results
        assertNotNull(result);
        assertEquals(1L, result.getOrderId());
        assertEquals("Placed", result.getStatus());

        // Verify that the save methods were actually called on our mock repositories
        verify(orderRepository, times(1)).save(any(OrderEntity.class));
        verify(ordersItemsRepository, times(1)).saveAll(any());
        
        // Verify that the cart was cleared
        verify(cartService, times(1)).clearCart(1L);
    }
        
    // =========================================================================
    // Test Cases for getOrderDetails()
    // =========================================================================

    @Test
    void getOrderDetails_ShouldReturnDetailsAndAvailableAgents() {
        // ARRANGE
        // Order Items Setup
        MenuItemEntity pizza = new MenuItemEntity();
        pizza.setName("Pepperoni Pizza");
        OrderItemEntity item1 = new OrderItemEntity();
        item1.setQuantity(1);
        item1.setPrice(50.0);
        item1.setMenuItem(pizza);
        placedOrder.setItems(List.of(item1));

        when(orderRepository.findByIdWithItems(1001L))
                .thenReturn(Optional.of(placedOrder));
        
        // Mock the dependency call to the DeliveryAgentService
        when(deliveryAgentService.findAvailableDeliveryAgents())
                .thenReturn(List.of(availableAgentDTO));

        // ACT
        OrderDetailsDTO result = ordersService.getOrderDetails(1001L);

        // ASSERT
        assertNotNull(result);
        assertEquals(placedOrder.getId(), result.getOrderId());
        assertEquals("PLACED", result.getOrderStatus());
        assertEquals(1, result.getItems().size());
        assertEquals(1, result.getAvailableAgents().size());
        
        verify(orderRepository, times(1)).findByIdWithItems(1001L);
        verify(deliveryAgentService, times(1)).findAvailableDeliveryAgents();
    }
    
    @Test
    void getOrderDetails_ShouldThrowException_WhenOrderNotFound() {
        // ARRANGE
        when(orderRepository.findByIdWithItems(9999L))
                .thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(OrderNotFoundException.class, () -> 
            ordersService.getOrderDetails(9999L));
        
        verify(orderRepository, times(1)).findByIdWithItems(9999L);
        verifyNoInteractions(deliveryAgentService);
    }
    
    // =========================================================================
    // Test Cases for assignAgent()
    // =========================================================================

    @Test
    void assignAgent_ShouldSuccessfullyAssignAgentAndUpdateStatuses() {
        // ARRANGE
        // Initial state mocks
        when(orderRepository.findById(placedOrder.getId()))
                .thenReturn(Optional.of(placedOrder));
        when(agentRepository.findById(agentAvailable.getId()))
                .thenReturn(Optional.of(agentAvailable));

        // Mock the save operations (return the entity after status update)
        when(orderRepository.save(any(OrderEntity.class))).thenAnswer(i -> i.getArguments()[0]);
        when(agentRepository.save(any(DeliveryAgentEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        // ACT
        OrderEntity result = ordersService.assignAgent(placedOrder.getId(), agentAvailable.getId());

        // ASSERT
        assertNotNull(result);
        assertEquals(agentAvailable.getId(), result.getAgent().getId(), "Order should be linked to the agent.");
        assertEquals("OUT FOR DELIVERY", result.getOrderStatus(), "Order status must change.");

        // Verify Agent Status change
        assertEquals("BUSY", agentAvailable.getStatus(), "Agent status must change to BUSY.");

        // Verify repository interactions
        verify(orderRepository, times(1)).findById(placedOrder.getId());
        verify(agentRepository, times(1)).findById(agentAvailable.getId());
        verify(orderRepository, times(1)).save(placedOrder);
        verify(agentRepository, times(1)).save(agentAvailable);
    }

    @Test
    void assignAgent_ShouldThrowException_WhenOrderNotFound() {
        // ARRANGE
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(OrderNotFoundException.class, () -> 
            ordersService.assignAgent(9999L, agentAvailable.getId()));
        
        verify(orderRepository, times(1)).findById(9999L);
        verify(agentRepository, never()).findById(anyLong());
    }

    @Test
    void assignAgent_ShouldThrowException_WhenAgentNotFound() {
        // ARRANGE
        when(orderRepository.findById(placedOrder.getId())).thenReturn(Optional.of(placedOrder));
        when(agentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(AgentListNotFoundException.class, () -> 
            ordersService.assignAgent(placedOrder.getId(), 9999L));

        verify(orderRepository, times(1)).findById(placedOrder.getId());
        verify(agentRepository, times(1)).findById(9999L);
    }

    @Test
    void assignAgent_ShouldThrowException_WhenOrderIsNotPlaced() {
        // ARRANGE
        placedOrder.setOrderStatus("DELIVERED"); // Change state to an invalid one
        when(orderRepository.findById(placedOrder.getId()))
                .thenReturn(Optional.of(placedOrder));
        when(agentRepository.findById(agentAvailable.getId()))
                .thenReturn(Optional.of(agentAvailable));

        // ACT & ASSERT
        assertThrows(AgentAssignmentException.class, () -> 
            ordersService.assignAgent(placedOrder.getId(), agentAvailable.getId()));

        verify(orderRepository, times(1)).findById(placedOrder.getId());
        verify(agentRepository, times(1)).findById(agentAvailable.getId());
        verify(orderRepository, never()).save(any());
        verify(agentRepository, never()).save(any());
    }

    @Test
    void assignAgent_ShouldThrowException_WhenAgentIsNotAvailable() {
        // ARRANGE
        // Agent is BUSY initially
        when(orderRepository.findById(placedOrder.getId()))
                .thenReturn(Optional.of(placedOrder));
        when(agentRepository.findById(agentBusy.getId()))
                .thenReturn(Optional.of(agentBusy));

        // ACT & ASSERT
        assertThrows(AgentAssignmentException.class, () -> 
            ordersService.assignAgent(placedOrder.getId(), agentBusy.getId()));

        verify(orderRepository, times(1)).findById(placedOrder.getId());
        verify(agentRepository, times(1)).findById(agentBusy.getId());
        verify(orderRepository, never()).save(any());
        verify(agentRepository, never()).save(any());
    }
    
    // =========================================================================
    // Test Cases for deliverOrder()
    // =========================================================================

    @Test
    void deliverOrder_ShouldMarkDelivered_UpdateAgentStats_AndSetAgentAvailable() {
        // ARRANGE
        OrderEntity outForDeliveryOrder = new OrderEntity();
        outForDeliveryOrder.setId(1003L);
        outForDeliveryOrder.setOrderStatus("OUT FOR DELIVERY");
        outForDeliveryOrder.setTotalAmount(200.00); // Higher amount for commission check
        outForDeliveryOrder.setAgent(agentBusy); // Agent is currently BUSY
        
        // Initial agent stats
        double initialTotalEarnings = agentBusy.getTotalEarnings(); // 50.0
        int initialTotalDeliveries = agentBusy.getTotalDeliveries(); // 5
        
        // Expected commission (15% of 200.00) = 30.00
        double expectedCommission = 30.00;
        
        when(orderRepository.findById(outForDeliveryOrder.getId()))
                .thenReturn(Optional.of(outForDeliveryOrder));

        // Mock the save operations
        when(orderRepository.save(any(OrderEntity.class))).thenAnswer(i -> i.getArguments()[0]);
        when(agentRepository.save(any(DeliveryAgentEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        // ACT
        OrderEntity result = ordersService.deliverOrder(outForDeliveryOrder.getId(), agentBusy.getId());

        // ASSERT
        assertNotNull(result);
        assertEquals("DELIVERED", result.getOrderStatus(), "Order status must be DELIVERED.");

        // Agent Status and Stats Verification
        assertEquals("AVAILABLE", agentBusy.getStatus(), "Agent status must return to AVAILABLE.");
        
        // Earnings: Initial (50.0) + Commission (30.00) = 80.00
        assertEquals(initialTotalEarnings + expectedCommission, agentBusy.getTotalEarnings(), 0.001, "Total earnings must be updated.");
        // Deliveries: Initial (5) + 1 = 6
        assertEquals(initialTotalDeliveries + 1, agentBusy.getTotalDeliveries(), "Total deliveries must be incremented.");
        // Todays Earning should also be updated by 30.00
        assertEquals(expectedCommission, agentBusy.getTodaysEarning(), 0.001, "Today's earning must be updated."); 
        
        verify(orderRepository, times(1)).findById(outForDeliveryOrder.getId());
        verify(orderRepository, times(1)).save(outForDeliveryOrder);
        verify(agentRepository, times(1)).save(agentBusy);
    }

    @Test
    void deliverOrder_ShouldRoundCommissionCorrectly() {
        // ARRANGE
        OrderEntity unroundedOrder = new OrderEntity();
        unroundedOrder.setId(1004L);
        unroundedOrder.setOrderStatus("OUT FOR DELIVERY");
        unroundedOrder.setTotalAmount(100.33); // Amount leading to unrounded commission: 15.0495
        unroundedOrder.setAgent(agentBusy); 

        // Expected rounded commission (15% of 100.33) = 15.05
        double expectedRoundedCommission = 15.05;
        
        when(orderRepository.findById(unroundedOrder.getId()))
                .thenReturn(Optional.of(unroundedOrder));

        when(orderRepository.save(any(OrderEntity.class))).thenAnswer(i -> i.getArguments()[0]);
        when(agentRepository.save(any(DeliveryAgentEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        // ACT
        ordersService.deliverOrder(unroundedOrder.getId(), agentBusy.getId());

        // ASSERT
        // Check the rounding of the commission
        assertEquals(expectedRoundedCommission, agentBusy.getTodaysEarning(), 0.001, "Commission must be rounded to two decimal places.");
    }
    
    @Test
    void deliverOrder_ShouldThrowException_WhenOrderNotFound() {
        // ARRANGE
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(OrderNotFoundException.class, () -> 
            ordersService.deliverOrder(9999L, agentBusy.getId()));
        
        verify(orderRepository, times(1)).findById(9999L);
        verify(orderRepository, never()).save(any());
        verify(agentRepository, never()).save(any());
    }

    @Test
    void deliverOrder_ShouldHandleNoAgentLinked_Gracefully() {
        // ARRANGE
        OrderEntity unassignedOrder = new OrderEntity();
        unassignedOrder.setId(1005L);
        unassignedOrder.setOrderStatus("OUT FOR DELIVERY");
        unassignedOrder.setTotalAmount(100.00);
        unassignedOrder.setAgent(null); // No agent linked

        when(orderRepository.findById(unassignedOrder.getId()))
                .thenReturn(Optional.of(unassignedOrder));
        when(orderRepository.save(any(OrderEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        // ACT
        OrderEntity result = assertDoesNotThrow(() -> 
            ordersService.deliverOrder(unassignedOrder.getId(), 0L)); // Agent ID is irrelevant here

        // ASSERT
        assertEquals("DELIVERED", result.getOrderStatus());
        
        // Agent repository save should *not* be called
        verify(orderRepository, times(1)).findById(unassignedOrder.getId());
        verify(orderRepository, times(1)).save(unassignedOrder);
        verify(agentRepository, never()).save(any());
    }
}
