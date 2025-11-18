package com.ofds.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ofds.dto.RestaurantDTO;
import com.ofds.entity.RestaurantEntity;
import com.ofds.exception.DataNotFoundException;
import com.ofds.mapper.RestaurantMapper;
import com.ofds.service.RestaurantService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth/restaurants")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final RestaurantMapper restaurantMapper;

    /**
     * Retrieves a list of all restaurant profiles available in the system.
     */
    @GetMapping("/getAllRestaurants")
    public ResponseEntity<List<RestaurantDTO>> getAllRestaurants() throws DataNotFoundException {
        List<RestaurantEntity> entities = restaurantService.getAllRestaurants();
        List<RestaurantDTO> dtos = entities.stream()
            .map(restaurantMapper::toDTO)
            .toList();
        return ResponseEntity.ok(dtos);
    }

    /**
     * Attempts to find a restaurant for login using their email and password combination.
     */
    @GetMapping("/{email}/{password}")
    public ResponseEntity<RestaurantEntity> getRestaurantByEmailAndPassword(@PathVariable String email,
                                                                            @PathVariable String password) throws DataNotFoundException {
        return ResponseEntity.ok(restaurantService.findByEmailAndPassword(email, password));
    }

    /**
     * Creates and saves a new restaurant entry in the database.
     */
    @PostMapping("/createRestaurant")
    public ResponseEntity<RestaurantEntity> createRestaurant(@RequestBody RestaurantEntity restaurantEntity) {
        RestaurantEntity saved = restaurantService.createRestaurant(restaurantEntity);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    /**
     * Deletes a restaurant profile from the system using its unique ID.
     */
    @DeleteMapping("/deleteRestaurant/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id) throws DataNotFoundException {
        return restaurantService.deleteRestaurant(id);
    }
}