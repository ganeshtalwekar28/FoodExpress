package com.ofds.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.ofds.entity.RestaurantEntity;

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

  @Test
  void findByEmailAndPassword_shouldReturnRestaurant() {
    Optional<RestaurantEntity> found = restaurantRepo.findByEmailAndPassword("idli@express.com", "sambar123");
    assertThat(found).isPresent();
    assertThat(found.get().getName()).isEqualTo("Idli Express");
  }
}
