package com.ofds.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartDTO {
    private Integer cartId;
    private Integer id;
    private Integer restaurantId;
    private String restaurantName;
    private Integer itemCount;
    private Double totalAmount;
    private List<CartItemDTO> items;
}
