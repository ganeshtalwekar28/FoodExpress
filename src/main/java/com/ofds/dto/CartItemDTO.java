package com.ofds.dto;

import lombok.Data;

@Data
public class CartItemDTO {
    private Long cartItemId;
    private Long menuItemId;
    private String name;
    private Double price;
    private Integer quantity;
    private String image_url;
}
