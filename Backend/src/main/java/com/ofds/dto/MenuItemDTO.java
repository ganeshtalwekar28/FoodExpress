package com.ofds.dto;

import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO used to transfer details about a single food item on a restaurant's menu, including its price and image.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemDTO {
    private Long id;
    private String name;
    private Double price;
    private String image_url;
    private Long restaurantId;
}