package com.ofds.mapper;

import com.ofds.dto.MenuItemDTO;
import com.ofds.dto.RestaurantDTO;
import com.ofds.entity.MenuItemEntity;
import com.ofds.entity.RestaurantEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RestaurantMapper {

    public RestaurantDTO toDTO(RestaurantEntity entity) {
        RestaurantDTO dto = new RestaurantDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setAddress(entity.getAddress());
        dto.setRating(entity.getRating());
        dto.setImage_url(entity.getImage_url());

        if (entity.getMenuItems() != null) {
            List<MenuItemDTO> menuItems = entity.getMenuItems().stream()
                .map(this::toMenuItemDTO)
                .collect(Collectors.toList());
            dto.setMenuItems(menuItems);
        }

        return dto;
    }

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
