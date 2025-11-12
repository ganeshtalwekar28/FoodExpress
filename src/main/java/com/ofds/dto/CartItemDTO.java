package com.ofds.dto;

import lombok.Data;

/**
 * DTO representing a single item within a customer's shopping cart, including its quantity and current price.
 */
@Data
public class CartItemDTO {
	
    private Long cartItemId;
    private Long menuItemId;
    private String name;
    private Double price;
    private Integer quantity;
    private String image_url;
}