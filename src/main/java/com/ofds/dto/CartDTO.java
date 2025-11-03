package com.ofds.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartDTO {
    private Long cartId; // Change the return type back to Integer if any issue occur.
    private Long id;
    private Long restaurantId;
    private String restaurantName;
    private Integer itemCount;
    private Double totalAmount;
    private List<CartItemDTO> items;
}
