package com.ofds.controller;

import com.ofds.dto.CartDTO;
import com.ofds.service.CartService;
import com.ofds.exception.DataNotFoundException;

import com.ofds.config.JwtUtils; 
import com.ofds.service.CustomerService; 
import com.ofds.service.CustomerUserDetailsService; 

import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService; 

import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user; 
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf; 
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
	@MockBean
    private CartService cartService;
    
    @MockBean
    private JwtUtils jwtUtils;
    @MockBean 
    private UserDetailsService userDetailsService; 
    @MockBean
    private CustomerService customerService; 
    @MockBean 
    private CustomerUserDetailsService customerUserDetailsService; 
    

    private CartDTO mockCartDTO() {
        CartDTO dto = new CartDTO();
        dto.setItemCount(2);
        dto.setTotalAmount(100.0);
        return dto;
    }

    private static final String CUSTOMER_ROLE = "CUSTOMER"; 

    private final org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor userDetails = 
        user("test").roles(CUSTOMER_ROLE);

    @Test
    void testGetCartByCustomer() throws Exception {
        Mockito.when(cartService.getCartByCustomerId(anyLong())).thenReturn(mockCartDTO());

        mockMvc.perform(get("/api/auth/carts/customer/1")
               .with(userDetails)) 
               .andExpect(status().isOk());
    }

    @Test
    void testAddItemToCart() throws Exception {
        Mockito.when(cartService.addItem(anyLong(), anyLong(), anyLong(), anyInt())).thenReturn(mockCartDTO());

        mockMvc.perform(post("/api/auth/carts/customer/1/restaurant/10/items/100")
                .param("quantity", "2")
                .with(userDetails)
                .with(csrf()))
               .andExpect(status().isCreated());
    }

    @Test
    void testUpdateItemQuantity() throws Exception {
        Mockito.when(cartService.updateQuantity(anyLong(), anyLong(), anyInt())).thenReturn(mockCartDTO());

        mockMvc.perform(put("/api/auth/carts/customer/1/items/1000")
                .param("quantity", "3")
                .with(userDetails)
                .with(csrf()))
               .andExpect(status().isOk());
    }

    @Test
    void testUpdateItemQuantity_CartDeleted() throws Exception {
        Mockito.when(cartService.updateQuantity(anyLong(), anyLong(), anyInt())).thenReturn(null);

        mockMvc.perform(put("/api/auth/carts/customer/1/items/1000")
                .param("quantity", "-3")
                .with(userDetails)
                .with(csrf()))
               .andExpect(status().isNoContent());
    }

    @Test
    void testRemoveItemFromCart() throws Exception {
        Mockito.when(cartService.updateQuantity(anyLong(), anyLong(), Mockito.eq(Integer.MIN_VALUE))).thenReturn(mockCartDTO());

        mockMvc.perform(delete("/api/auth/carts/customer/1/items/1000")
               .with(userDetails)
               .with(csrf()))
               .andExpect(status().isOk()); 
    }

    @Test
    void testRemoveItemFromCart_CartDeleted() throws Exception {
        Mockito.when(cartService.updateQuantity(anyLong(), anyLong(), Mockito.eq(Integer.MIN_VALUE))).thenReturn(null);

        mockMvc.perform(delete("/api/auth/carts/customer/1/items/1000")
               .with(userDetails)
               .with(csrf())) 
               .andExpect(status().isNoContent()); 
    }

    @Test
    void testClearCart() throws Exception {
        Mockito.doNothing().when(cartService).clearCart(anyLong());

        mockMvc.perform(delete("/api/auth/carts/customer/1")
               .with(userDetails)
               .with(csrf())) 
               .andExpect(status().isNoContent());
    }
        
    @Test
    void testGetCartByCustomer_NoCartFound() throws Exception {
        Mockito.when(cartService.getCartByCustomerId(anyLong()))
               .thenThrow(new DataNotFoundException("Cart not found for customer."));

        mockMvc.perform(get("/api/auth/carts/customer/1")
               .with(userDetails)) 
               .andExpect(status().isNotFound());
    }
    
    @Test
    void testAddItemToCart_ItemNotFound() throws Exception {
        Mockito.when(cartService.addItem(anyLong(), anyLong(), anyLong(), anyInt()))
               .thenThrow(new DataNotFoundException("Food item or restaurant not found."));

        mockMvc.perform(post("/api/auth/carts/customer/1/restaurant/10/items/100")
                .param("quantity", "2")
                .with(userDetails)
                .with(csrf())) 
               .andExpect(status().isNotFound()); 
    }
    
    @Test
    void testUpdateItemQuantity_NoItemInCart() throws Exception {
        Mockito.when(cartService.updateQuantity(anyLong(), anyLong(), anyInt()))
               .thenThrow(new DataNotFoundException("Item not found in cart."));

        // PUT request - CSRF needed
        mockMvc.perform(put("/api/auth/carts/customer/1/items/1000")
                .param("quantity", "3")
                .with(userDetails)
                .with(csrf()))
               .andExpect(status().isNotFound()); 
    }
}