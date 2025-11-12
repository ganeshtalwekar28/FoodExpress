package com.ofds.service;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ofds.dto.MenuItemDTO;
import com.ofds.entity.MenuItemEntity;
import com.ofds.entity.RestaurantEntity;
import com.ofds.exception.DataNotFoundException;
import com.ofds.repository.MenuItemRepository;
import com.ofds.repository.RestaurantRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class for handling all business logic related to menu items, 
 * including CRUD operations and searching within a restaurant context.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MenuItemService {

	@Autowired
     MenuItemRepository menuItemRepository;
    
	@Autowired
	RestaurantRepository restaurantRepository;

	@Autowired
	ModelMapper modelMapper;

	/**
	 * Retrieves all menu items belonging to a specific restaurant ID and maps them to a list of DTOs.
	 */
	public ResponseEntity<List<MenuItemDTO>> getMenuItemsByRestaurantId(Long restaurantId) throws DataNotFoundException {
		Optional<RestaurantEntity> restaurantOpt = restaurantRepository.findById(restaurantId);
	    if (restaurantOpt.isEmpty()) {
	    	throw new DataNotFoundException("Restaurant not found with id: " + restaurantId);
	    } else {
	    	List<MenuItemEntity> listEntity = restaurantOpt.get().getMenuItems();
         	List<MenuItemDTO> dtolst = listEntity.stream()
	    		    .map(itemEntity -> modelMapper.map(itemEntity, MenuItemDTO.class))
	    		    .toList();

	 	    return new ResponseEntity<>(dtolst, HttpStatus.OK);
	    }
	}
	
	/**
	 * Creates a new menu item and associates it with the specified restaurant ID.
	 */
    public ResponseEntity<MenuItemDTO> createMenuItem(Long restaurantId, MenuItemDTO dto) throws DataNotFoundException {
        Optional<RestaurantEntity> restaurantOpt = restaurantRepository.findById(restaurantId);
        if (restaurantOpt.isEmpty()) {
            throw new DataNotFoundException("Restaurant not found with id: " + restaurantId);
        }

        MenuItemEntity item = modelMapper.map(dto, MenuItemEntity.class);
        item.setRestaurant(restaurantOpt.get());

        MenuItemEntity saved = menuItemRepository.save(item);
        MenuItemDTO responseDto = modelMapper.map(saved, MenuItemDTO.class);
        responseDto.setRestaurantId(restaurantId);
        responseDto.setName(dto.getName());
        responseDto.setPrice(dto.getPrice());
        
        log.info("Received DTO: " + responseDto);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * Updates the details of an existing menu item identified by its ID.
     */
    public ResponseEntity<MenuItemDTO> updateMenuItem(Long id, MenuItemDTO dto) throws DataNotFoundException {
        Optional<MenuItemEntity> itemOpt = menuItemRepository.findById(id);
        if (itemOpt.isEmpty()) {
            throw new DataNotFoundException("Menu item not found with id: " + id);
        }

        MenuItemEntity item = itemOpt.get();
        item.setName(dto.getName());
        item.setPrice(dto.getPrice());
        item.setImage_url(dto.getImage_url());

        MenuItemEntity updated = menuItemRepository.save(item);
        MenuItemDTO responseDto = modelMapper.map(updated, MenuItemDTO.class);
        responseDto.setRestaurantId(updated.getRestaurant().getId());

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * Deletes a menu item identified by its ID.
     */
    public ResponseEntity<Void> deleteMenuItem(Long id) throws DataNotFoundException {
        Optional<MenuItemEntity> itemOpt = menuItemRepository.findById(id);
        if (itemOpt.isEmpty()) {
            throw new DataNotFoundException("Menu item not found with id: " + id);
        }

        menuItemRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Searches for menu items whose name contains the specified query string (case-insensitive).
     */
    public ResponseEntity<List<MenuItemDTO>> searchMenuItemsByName(String query) throws DataNotFoundException {
        
        List<MenuItemEntity> listEntity = menuItemRepository.findByNameContainingIgnoreCase(query);
        
        if (listEntity.isEmpty()) {
            throw new DataNotFoundException("No menu items found matching the search query: " + query);
        }

        // Convert Entities to DTOs and set the restaurant ID
        List<MenuItemDTO> dtolst = listEntity.stream()
            .map(itemEntity -> {
                MenuItemDTO dto = modelMapper.map(itemEntity, MenuItemDTO.class);
                
                if (itemEntity.getRestaurant() != null) {
                    dto.setRestaurantId(itemEntity.getRestaurant().getId());
                }
                return dto;
            })
            .toList();

        return new ResponseEntity<>(dtolst, HttpStatus.OK);
    }  
}