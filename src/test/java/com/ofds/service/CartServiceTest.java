package com.ofds.service;

import com.ofds.dto.CartDTO;
import com.ofds.entity.*;
import com.ofds.exception.DataNotFoundException;
import com.ofds.mapper.CartMapper;
import com.ofds.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) 
class CartServiceTest {

    @InjectMocks 
    private CartService cartService;

    @Mock private CartRepository cartRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private RestaurantRepository restaurantRepository;
    @Mock private MenuItemRepository menuItemRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private CartMapper cartMapper;

    private CustomerEntity customer;
    private RestaurantEntity restaurant;
    private MenuItemEntity menuItem;
    private CartEntity cart;
    private CartItemEntity cartItem;
    private CartDTO cartDTO;

    @BeforeEach
    void setup() {
        customer = new CustomerEntity();
        customer.setId(1L);

        restaurant = new RestaurantEntity();
        restaurant.setId(10L);

        menuItem = new MenuItemEntity();
        menuItem.setId(100L);
        menuItem.setPrice(50.0);

        cartItem = new CartItemEntity();
        cartItem.setId(1000L);
        cartItem.setMenuItem(menuItem);
        cartItem.setQuantity(2);

        cart = new CartEntity();
        cart.setId(500L);
        cart.setCustomer(customer);
        cart.setRestaurant(restaurant);
        cart.setItems(new ArrayList<>(List.of(cartItem)));
        cart.setItemCount(2);
        cart.setTotalAmount(100.0);

        cartDTO = new CartDTO();
    }

    @Test
    void testGetCartByCustomerId() throws DataNotFoundException {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer)); 
        when(cartRepository.findByCustomer(any())).thenReturn(Optional.of(cart));
        when(cartMapper.toDTO(any())).thenReturn(cartDTO);

        CartDTO result = cartService.getCartByCustomerId(1L);
        assertNotNull(result);
        verify(customerRepository).findById(1L);
        verify(cartRepository).findByCustomer(customer);
    }

    @Test
    void testAddItem_NewCart() throws DataNotFoundException {
        // Arrange
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));
        when(restaurantRepository.findById(anyLong())).thenReturn(Optional.of(restaurant));
        when(menuItemRepository.findById(anyLong())).thenReturn(Optional.of(menuItem));
        when(cartRepository.findByCustomer(any())).thenReturn(Optional.empty());

        CartEntity newCart = new CartEntity();
        newCart.setCustomer(customer);
        newCart.setRestaurant(restaurant);
        newCart.setItems(new ArrayList<>()); 
        when(cartRepository.save(any())).thenReturn(newCart);

        when(cartItemRepository.save(any())).thenAnswer(invocation -> {
            CartItemEntity item = invocation.getArgument(0);
            item.setId(999L); 
            return item;
        });

        when(cartMapper.toDTO(any())).thenReturn(cartDTO);

        // Act
        CartDTO result = cartService.addItem(1L, 10L, 100L, 2);

        // Assert
        assertNotNull(result);
     
        verify(cartMapper).toDTO(any());
    }

    

    @Test
    void testUpdateQuantity_Increase() throws DataNotFoundException {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomer(any())).thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(anyLong())).thenReturn(Optional.of(cartItem));
        when(cartMapper.toDTO(any())).thenReturn(cartDTO);

        CartDTO result = cartService.updateQuantity(1L, 1000L, 1);
        assertNotNull(result);
        assertEquals(3, cartItem.getQuantity());
        verify(cartItemRepository).save(cartItem);
    }

    @Test
    void testUpdateQuantity_RemoveItem() throws DataNotFoundException {
        cartItem.setQuantity(1); 
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomer(any())).thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(anyLong())).thenReturn(Optional.of(cartItem));

        CartDTO result = cartService.updateQuantity(1L, 1000L, -1);
        assertNull(result);
        verify(cartItemRepository).delete(cartItem);
        verify(cartRepository).delete(cart);
    }

    @Test
    void testClearCart() throws DataNotFoundException {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomer(any())).thenReturn(Optional.of(cart));

        cartService.clearCart(1L);
        verify(cartItemRepository).deleteAll(cart.getItems());
        verify(cartRepository).delete(cart);
    }
}
