package com.ofds.dto;

import lombok.Data;

@Data
public class CartItemDTO {
    private Integer cartItemId;
    private Integer menuItemId;
    private String name;
    private Double price;
    private Integer quantity;
    private String image_url;
}
