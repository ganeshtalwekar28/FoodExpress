package com.ofds.config;

import com.ofds.dto.CartDTO;
import com.ofds.dto.CartItemDTO;
import com.ofds.entity.CartEntity;
import com.ofds.entity.CartItemEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartMapper {

    public CartDTO toDTO(CartEntity cart) {
        CartDTO dto = new CartDTO();
        dto.setCartId(cart.getId());
        dto.setId(cart.getCustomer().getId());
        dto.setRestaurantId(cart.getRestaurant().getId());
        dto.setRestaurantName(cart.getRestaurant().getName());
        dto.setItemCount(cart.getItemCount());
        dto.setTotalAmount(cart.getTotalAmount());

        List<CartItemDTO> items = cart.getItems().stream().map(this::toItemDTO).collect(Collectors.toList());
        dto.setItems(items);

        return dto;
    }

    public CartItemDTO toItemDTO(CartItemEntity item) {
        CartItemDTO dto = new CartItemDTO();
        dto.setCartItemId(item.getId());
        dto.setMenuItemId(item.getMenuItem().getId());
        dto.setName(item.getMenuItem().getName());
        dto.setPrice(item.getMenuItem().getPrice());
        dto.setImage_url(item.getMenuItem().getImage_url());
        dto.setQuantity(item.getQuantity());
        return dto;
    }
}
