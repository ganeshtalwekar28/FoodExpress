package com.ofds.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ofds.entity.RestaurantEntity;
import com.ofds.exception.DataNotFoundException;
import com.ofds.repository.RestaurantRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service class for handling all business logic related to Restaurant entities, 
 * including authentication, creation, retrieval, and deletion.
 */
@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepo;

    /**
     * Authenticates a restaurant owner by finding a restaurant entity matching the given email and password.
     */
    
    public RestaurantEntity findByEmailAndPassword(String email, String password) {
    	System.out.println("Inside Find Function");
  	    // Throws a NoSuchElementException if no restaurant is found
  	    return restaurantRepo.findByEmailAndPassword(email, password).orElseThrow();
   }
    
    /**
     * Saves a new RestaurantEntity to the database.
     */
    public RestaurantEntity createRestaurant(RestaurantEntity restaurantEntity) {
        return restaurantRepo.save(restaurantEntity);
    }

    /**
     * Retrieves all restaurant entities from the database.
     */
    public List<RestaurantEntity> getAllRestaurants() throws DataNotFoundException {
        List<RestaurantEntity> restaurants = restaurantRepo.findAll();
        if (restaurants.isEmpty()) {
            throw new DataNotFoundException("No restaurants found");
        }
        return restaurants;
    }

    /**
     * Deletes a restaurant entity based on its ID after verifying its existence.
     */
    public ResponseEntity<Void> deleteRestaurant(Long id) throws DataNotFoundException {
        Optional<RestaurantEntity> optionalRestaurant = restaurantRepo.findById(id);
        if (optionalRestaurant.isEmpty()) {
            throw new DataNotFoundException("Restaurant not found with id: " + id);
        }
        restaurantRepo.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}