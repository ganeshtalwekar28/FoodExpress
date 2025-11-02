package com.ofds.dto;

import lombok.Data;

@Data
public class OrderItemDTO {
    private String name;
    private Double price;
    private Integer quantity;
    private String image_url;
}