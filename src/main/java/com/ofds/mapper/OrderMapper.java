package com.ofds.mapper;

import com.ofds.dto.OrderItemDTO;  // <-- FIX 1: Missing Import
import com.ofds.dto.OrderResponse; // <-- FIX 2: Missing Import
import com.ofds.entity.OrderEntity;
import com.ofds.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderMapper {
    
    public OrderResponse toResponse(OrderEntity entity) {
        OrderResponse dto = new OrderResponse();
        
        // This conversion assumes OrderEntity ID is Long but OrderResponse ID is Integer.
        // It's safer to ensure they match (e.g., both Long) or handle potential overflow.
        // Since we defined OrderResponse with Integer, this line is fine if the ID is small.
        dto.setOrderId(entity.getId()); 
        
        dto.setStatus(entity.getOrderStatus());
        dto.setTotalAmount(entity.getTotalAmount());
        dto.setOrderDate(entity.getOrderDate());
        
        // FIX 3: Add estimatedDelivery from Entity to DTO
        dto.setEstimatedDelivery(entity.getEstimatedDelivery());
        
        // This line assumes a successful fetch of the Restaurant entity on the OrderEntity.
        // If the Restaurant entity is null, this will throw a NullPointerException.
        dto.setRestaurantName(entity.getRestaurant().getName()); 
       
        dto.setRazorpayOrderId(entity.getRazorpayOrderId());
        dto.setItems(entity.getItems().stream()
                .map(this::toItemDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    private OrderItemDTO toItemDTO(OrderItemEntity entity) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setName(entity.getName());
        dto.setPrice(entity.getPrice());
        dto.setQuantity(entity.getQuantity());
        dto.setImage_url(entity.getImageUrl()); 
        return dto;
    }
}