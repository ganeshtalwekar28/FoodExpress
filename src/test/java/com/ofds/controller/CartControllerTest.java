package com.ofds.controller;

import com.ofds.dto.CartDTO;
import com.ofds.service.CartService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
	@MockBean
    private CartService cartService;

    private CartDTO mockCartDTO() {
        CartDTO dto = new CartDTO();
        dto.setItemCount(2);
        dto.setTotalAmount(100.0);
        return dto;
    }

    @Test
    void testGetCartByCustomer() throws Exception {
        Mockito.when(cartService.getCartByCustomerId(anyLong())).thenReturn(mockCartDTO());

        mockMvc.perform(get("/api/carts/customer/1"))
               .andExpect(status().isOk());
    }

    @Test
    void testAddItemToCart() throws Exception {
        Mockito.when(cartService.addItem(anyLong(), anyLong(), anyLong(), anyInt())).thenReturn(mockCartDTO());

        mockMvc.perform(post("/api/carts/customer/1/restaurant/10/items/100")
                .param("quantity", "2"))
               .andExpect(status().isCreated());
    }

    @Test
    void testUpdateItemQuantity() throws Exception {
        Mockito.when(cartService.updateQuantity(anyLong(), anyLong(), anyInt())).thenReturn(mockCartDTO());

        mockMvc.perform(put("/api/carts/customer/1/items/1000")
                .param("quantity", "3"))
               .andExpect(status().isOk());
    }

    @Test
    void testUpdateItemQuantity_CartDeleted() throws Exception {
        Mockito.when(cartService.updateQuantity(anyLong(), anyLong(), anyInt())).thenReturn(null);

        mockMvc.perform(put("/api/carts/customer/1/items/1000")
                .param("quantity", "-3"))
               .andExpect(status().isNoContent());
    }

    @Test
    void testRemoveItemFromCart() throws Exception {
        Mockito.when(cartService.updateQuantity(anyLong(), anyLong(), Mockito.eq(Integer.MIN_VALUE))).thenReturn(mockCartDTO());

        mockMvc.perform(delete("/api/carts/customer/1/items/1000"))
               .andExpect(status().isOk());
    }

    @Test
    void testRemoveItemFromCart_CartDeleted() throws Exception {
        Mockito.when(cartService.updateQuantity(anyLong(), anyLong(), Mockito.eq(Integer.MIN_VALUE))).thenReturn(null);

        mockMvc.perform(delete("/api/carts/customer/1/items/1000"))
               .andExpect(status().isNoContent());
    }

    @Test
    void testClearCart() throws Exception {
        Mockito.doNothing().when(cartService).clearCart(anyLong());

        mockMvc.perform(delete("/api/carts/customer/1"))
               .andExpect(status().isNoContent());
    }
}
