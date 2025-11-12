package com.ofds.dto;
 
import java.util.List;
 
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
/**
 * DTO used to transfer comprehensive data about a restaurant, including its details, rating, 
 * cuisine type, and a list of its menu items.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDTO {
    private Long id;
    private String name;
    private String owner_name;
    private String email;
    private String phone;
    private String address;
    private Double rating;
    private String cuisine_type;
    private String image_url;
    private List<MenuItemDTO>menuItems;
}