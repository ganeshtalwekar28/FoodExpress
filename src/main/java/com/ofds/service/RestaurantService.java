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
//
//@Service
//@RequiredArgsConstructor
//public class RestaurantService {
//
//	@Autowired
//	RestaurantRepository restaurantRepo;
//	
//
//	//Create New Restaurant
//    public ResponseEntity<RestaurantEntity> createRestaurant(RestaurantEntity restaurantEntity) {
//        RestaurantEntity saved = restaurantRepo.save(restaurantEntity);
//        return new ResponseEntity<>(saved, HttpStatus.CREATED);
//    }
//    
//
//    //Display List Of Restaurants
//    public ResponseEntity<List<RestaurantEntity>> getAllRestaurants() throws DataNotFoundException {
//        List<RestaurantEntity> restaurants = restaurantRepo.findAll();
//        if (restaurants.isEmpty()) {
//            throw new DataNotFoundException("No restaurants found");
//        }
//        return new ResponseEntity<>(restaurants, HttpStatus.OK);
//    }
//
//
//    //Delete existing restaurant
//    public ResponseEntity<Void> deleteRestaurant(Integer id) throws DataNotFoundException {
//        Optional<RestaurantEntity> optionalRestaurant = restaurantRepo.findById(id);
//        if (optionalRestaurant.isEmpty()) {
//            throw new DataNotFoundException("Restaurant not found with id: " + id);
//        }
//        restaurantRepo.deleteById(id);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
//    
//    //Owner Login using Email and Password
//    public RestaurantEntity findByEmailAndPassword(String email, String password) {
//    	System.out.println("Inside Find Function");
//    	RestaurantEntity newRes = restaurantRepo.findByEmailAndPassword(email, password).orElseThrow();
//    	
////    	System.out.println("Got the Restaurant:" + newRes);
//    	return newRes;
//    }
//
//  }
//


@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepo;

    public RestaurantEntity findByEmailAndPassword(String email, String password) {
    	System.out.println("Inside Find Function");
  	RestaurantEntity newRes = restaurantRepo.findByEmailAndPassword(email, password).orElseThrow();
   	
////    	System.out.println("Got the Restaurant:" + newRes);
	return newRes;
   }
    public RestaurantEntity createRestaurant(RestaurantEntity restaurantEntity) {
        return restaurantRepo.save(restaurantEntity);
    }

    public List<RestaurantEntity> getAllRestaurants() throws DataNotFoundException {
        List<RestaurantEntity> restaurants = restaurantRepo.findAll();
        if (restaurants.isEmpty()) {
            throw new DataNotFoundException("No restaurants found");
        }
        return restaurants;
    }

    public ResponseEntity<Void> deleteRestaurant(Integer id) throws DataNotFoundException {
        Optional<RestaurantEntity> optionalRestaurant = restaurantRepo.findById(id);
        if (optionalRestaurant.isEmpty()) {
            throw new DataNotFoundException("Restaurant not found with id: " + id);
        }
        restaurantRepo.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
