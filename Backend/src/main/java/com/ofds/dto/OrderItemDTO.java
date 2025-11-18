package com.ofds.dto;

import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO used to represent a single food item that belongs to a placed order, including its name, price, and quantity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
	
    private String name;
    private Double price;
    private Integer quantity;
    private String image_url;
    
    public OrderItemDTO(String name, Double price, Integer quantity) {
    	this.name = name;
    	this.price = price;
    	this.quantity = quantity;
    }
}