package com.ofds.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ofds.dto.MenuItemDTO;
import com.ofds.exception.DataNotFoundException;
import com.ofds.service.MenuItemService;
import com.ofds.service.RestaurantService;

import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/auth/menu-items")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class MenuItemController {

	@Autowired
	MenuItemService menuItemService;
	
	@Autowired
	RestaurantService restaurantService;

    /**
     * Retrieves all menu items belonging to a specific restaurant ID.
     */
    @GetMapping("/getMenuItemsByRestaurantId/restaurant/{restaurantId}")
    public ResponseEntity<List<MenuItemDTO>> getMenuItemsByRestaurantId(@PathVariable Long restaurantId) throws DataNotFoundException {
        return menuItemService.getMenuItemsByRestaurantId(restaurantId);
    }
    
    /**
     * Creates and adds a new menu item to a specified restaurant's menu.
     */
    @PostMapping("/createMenuItem/restaurant/{restaurantId}")
    public ResponseEntity<MenuItemDTO> createMenuItem(
            @PathVariable Long restaurantId,
            @RequestBody MenuItemDTO dto) throws DataNotFoundException {
        return menuItemService.createMenuItem(restaurantId, dto);
    }
    
    /**
     * Updates the details of an existing menu item using its ID.
     */
    @PutMapping("/updateMenuItem/{id}")
    public ResponseEntity<MenuItemDTO> updateMenuItem(
            @PathVariable Long id,
            @RequestBody MenuItemDTO dto) throws DataNotFoundException {
        return menuItemService.updateMenuItem(id, dto);
    }

    /**
     * Removes a menu item from the system using its ID.
     */
    @DeleteMapping("/deleteMenuItem/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) throws DataNotFoundException {
        return menuItemService.deleteMenuItem(id);
    }
    
    /**
     * Searches for menu items whose names match the given query string.
     */
    @GetMapping("/search") 
    public ResponseEntity<List<MenuItemDTO>> searchMenuItemsByName(@RequestParam String query) throws DataNotFoundException {
        return menuItemService.searchMenuItemsByName(query);
    }
}