package com.ofds.mapper;

import com.ofds.dto.OrderItemDTO;  
import com.ofds.dto.OrderResponse; 
import com.ofds.entity.OrderEntity;
import com.ofds.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderMapper {
    
    /**
     * Converts an OrderEntity into an OrderResponse DTO, used to confirm a successful order placement to the client.
     */
    public OrderResponse toResponse(OrderEntity entity) {
        OrderResponse dto = new OrderResponse();
        
        dto.setOrderId(entity.getId()); 
        
        dto.setStatus(entity.getOrderStatus());
        dto.setTotalAmount(entity.getTotalAmount());
        dto.setOrderDate(entity.getOrderDate());
        
        dto.setEstimatedDelivery(entity.getEstimatedDelivery());
        
        dto.setRestaurantName(entity.getRestaurant().getName()); 
       
        dto.setRazorpayOrderId(entity.getRazorpayOrderId());
        dto.setItems(entity.getItems().stream()
                .map(this::toItemDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    /**
     * Converts an OrderItemEntity into a simplified OrderItemDTO.
     */
    private OrderItemDTO toItemDTO(OrderItemEntity entity) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setName(entity.getName());
        dto.setPrice(entity.getPrice());
        dto.setQuantity(entity.getQuantity());
        dto.setImage_url(entity.getImage_url()); 
        return dto;
    }
}