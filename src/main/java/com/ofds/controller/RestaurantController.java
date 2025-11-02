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

//@RestController
//@RequestMapping("/api/restaurants")
//@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:4200")
//public class RestaurantController {
//
//	@Autowired
//	RestaurantService restaurantService;
//
//	@GetMapping("/getAllRestaurants")
//	public ResponseEntity<List<RestaurantEntity>> getAllRestaurants() throws DataNotFoundException {
//		return restaurantService.getAllRestaurants();
//	}
//
//	@GetMapping("/{email}/{password}")
//	public ResponseEntity<RestaurantEntity> getRestaurantByEmailAndPassword(@PathVariable String email,
//			@PathVariable String password) {
//		return ResponseEntity.ok(restaurantService.findByEmailAndPassword(email, password));
//	}
//
//	@PostMapping("/createRestaurant")
//	public ResponseEntity<RestaurantEntity> createRestaurant(@RequestBody RestaurantEntity restaurantEntity) {
//		return restaurantService.createRestaurant(restaurantEntity);
//	}
//
//	@DeleteMapping("/deleteRestaurant/{id}")
//	public ResponseEntity<Void> deleteRestaurant(@PathVariable Integer id) throws DataNotFoundException {
//		return restaurantService.deleteRestaurant(id);
//	}
//
//}


@RestController
@RequestMapping("/api/auth/restaurants")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final RestaurantMapper restaurantMapper;

    @GetMapping("/getAllRestaurants")
    public ResponseEntity<List<RestaurantDTO>> getAllRestaurants() throws DataNotFoundException {
        List<RestaurantEntity> entities = restaurantService.getAllRestaurants();
        List<RestaurantDTO> dtos = entities.stream()
            .map(restaurantMapper::toDTO)
            .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{email}/{password}")
    public ResponseEntity<RestaurantEntity> getRestaurantByEmailAndPassword(@PathVariable String email,
                                                                            @PathVariable String password) {
        return ResponseEntity.ok(restaurantService.findByEmailAndPassword(email, password));
    }

    @PostMapping("/createRestaurant")
    public ResponseEntity<RestaurantEntity> createRestaurant(@RequestBody RestaurantEntity restaurantEntity) {
        RestaurantEntity saved = restaurantService.createRestaurant(restaurantEntity);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @DeleteMapping("/deleteRestaurant/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Integer id) throws DataNotFoundException {
        return restaurantService.deleteRestaurant(id);
    }
}
