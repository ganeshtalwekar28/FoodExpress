package com.ofds.mapper;

import com.ofds.dto.MenuItemDTO;
import com.ofds.dto.RestaurantDTO;
import com.ofds.entity.MenuItemEntity;
import com.ofds.entity.RestaurantEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class responsible for converting data between the RestaurantEntity persistence model 
 * and the RestaurantDTO data transfer object for client consumption.
 */
@Component
public class RestaurantMapper {

    /**
     * Converts a RestaurantEntity object into a complete RestaurantDTO, including the list of menu items.
     */
    public RestaurantDTO toDTO(RestaurantEntity entity) {
        RestaurantDTO dto = new RestaurantDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setAddress(entity.getAddress());
        dto.setRating(entity.getRating());
        dto.setCuisine_type(entity.getCuisine_type());
        dto.setImage_url(entity.getImage_url());

        if (entity.getMenuItems() != null) {
            List<MenuItemDTO> menuItems = entity.getMenuItems().stream()
                .map(this::toMenuItemDTO)
                .collect(Collectors.toList());
            dto.setMenuItems(menuItems);
        }

        return dto;
    }

    /**
     * Converts a single MenuItemEntity object into a MenuItemDTO.
     */
    private MenuItemDTO toMenuItemDTO(MenuItemEntity item) {
        MenuItemDTO dto = new MenuItemDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setPrice(item.getPrice());
        dto.setImage_url(item.getImage_url());
        dto.setRestaurantId(item.getRestaurant().getId());
        return dto;
    }
}