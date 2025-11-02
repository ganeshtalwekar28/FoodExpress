package com.ofds.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDTO {
    private Integer id;
    private String name;
    //private String ownerName;
    //private String email;
    //private String phone;
    private String address;
    private Double rating;
    private String image_url;
    private List<MenuItemDTO>menuItems;
}