package com.ofds.dto;

import lombok.Data;

import java.util.List;

/**
 * DTO used to represent the customer's shopping cart, including total summary and a list of items inside it.
 */
@Data
public class CartDTO {
    private Long cartId; 
    private Long id;
    private Long restaurantId;
    private String restaurantName;
    private Integer itemCount;
    private Double totalAmount;
    private List<CartItemDTO> items;
}