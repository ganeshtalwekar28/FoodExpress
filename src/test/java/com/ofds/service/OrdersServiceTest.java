package com.ofds.service;

import com.ofds.dto.CartDTO;
import com.ofds.dto.CartItemDTO;
import com.ofds.dto.OrderRequest;
import com.ofds.dto.OrderResponse;
import com.ofds.entity.CustomerEntity;
import com.ofds.entity.MenuItemEntity;
import com.ofds.entity.OrderEntity;
import com.ofds.entity.RestaurantEntity;
import com.ofds.exception.DataNotFoundException;
import com.ofds.mapper.OrderMapper;
import com.ofds.repository.CustomerRepository;
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

import java.util.Collections;
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

    @InjectMocks
    private OrdersService ordersService;

    private OrderRequest orderRequest;
    private CartDTO cartDTO;
    private CustomerEntity customer;
    private RestaurantEntity restaurant;
    private MenuItemEntity menuItem;
    private OrderEntity savedOrder;
    private OrderResponse orderResponse;

    @BeforeEach
    void setUp() {
        // 1. Setup dummy data that will be returned by our mocks
        customer = new CustomerEntity();
        customer.setId(1);
        customer.setName("Test User");

        restaurant = new RestaurantEntity();
        restaurant.setId(1);
        restaurant.setName("Test Restaurant");

        menuItem = new MenuItemEntity();
        menuItem.setId(101);
        menuItem.setName("Test Dish");
        menuItem.setPrice(100.0);

        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setMenuItemId(101);
        cartItemDTO.setName("Test Dish");
        cartItemDTO.setPrice(100.0);
        cartItemDTO.setQuantity(2);

        cartDTO = new CartDTO();
        cartDTO.setRestaurantId(1);
        cartDTO.setItems(Collections.singletonList(cartItemDTO));

        orderRequest = new OrderRequest();
        orderRequest.setCustomerId(1);
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
    }

    @Test
    void placeOrder_Success() throws DataNotFoundException {
        // 2. Define the behavior of our mocks
        when(cartService.getCartByCustomerId(1)).thenReturn(cartDTO);
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurant));
        
        // This is the tricky part that was likely failing before
//        when(cartService.getMenuItemRepository()).thenReturn(menuItemRepository);
        when(menuItemRepository.findById(101)).thenReturn(Optional.of(menuItem));
        
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
        verify(cartService, times(1)).clearCart(1);
    }
}
