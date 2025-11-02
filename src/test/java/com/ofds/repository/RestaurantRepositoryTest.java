package com.ofds.repository;

import com.ofds.entity.RestaurantEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RestaurantRepositoryTest {

  @Autowired
  private RestaurantRepository restaurantRepo;

  private RestaurantEntity restaurant;

  @BeforeEach
  void setUp() {
    restaurant = new RestaurantEntity();
    restaurant.setName("Idli Express");
    restaurant.setEmail("idli@express.com");
    restaurant.setPassword("sambar123");
    restaurant = restaurantRepo.save(restaurant);
  }

  @Test
  void save_shouldPersistRestaurant() {
    assertThat(restaurant.getId()).isNotNull();
    assertThat(restaurant.getName()).isEqualTo("Idli Express");
  }

  @Test
  void findById_shouldReturnRestaurant() {
    Optional<RestaurantEntity> found = restaurantRepo.findById(restaurant.getId());
    assertThat(found).isPresent();
    assertThat(found.get().getEmail()).isEqualTo("idli@express.com");
  }

//  @Test
//  void findByEmail_shouldReturnList() {
//    List<RestaurantEntity> found = restaurantRepo.findByEmail("idli@express.com");
//    assertThat(found).isNotEmpty();
//    assertThat(found.get(0).getName()).isEqualTo("Idli Express");
//  }

  @Test
  void findByEmailAndPassword_shouldReturnRestaurant() {
    Optional<RestaurantEntity> found = restaurantRepo.findByEmailAndPassword("idli@express.com", "sambar123");
    assertThat(found).isPresent();
    assertThat(found.get().getName()).isEqualTo("Idli Express");
  }
}
