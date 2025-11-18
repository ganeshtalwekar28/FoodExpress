package com.ofds.repository;

import com.ofds.entity.MenuItemEntity;
import com.ofds.entity.RestaurantEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MenuItemRepositoryTest {

  @Autowired
  private MenuItemRepository menuItemRepo;

  @Autowired
  private RestaurantRepository restaurantRepo;

  private MenuItemEntity item;
  private RestaurantEntity restaurant;

  @BeforeEach
  void setUp() {
    restaurant = new RestaurantEntity();
    restaurant.setName("Testaurant");
    restaurant.setEmail("owner@example.com");
    restaurant.setPassword("secret");
    restaurant = restaurantRepo.save(restaurant); 

    item = new MenuItemEntity();
    item.setName("Idli");
    item.setPrice(30.0);
    item.setImage_url("http://example.com/idli.jpg");
    item.setRestaurant(restaurant);

    item = menuItemRepo.save(item); 
  }

  @Test
  void save_shouldPersistMenuItem() {
    assertThat(item.getId()).isNotNull();
    assertThat(item.getName()).isEqualTo("Idli");
    assertThat(item.getRestaurant().getName()).isEqualTo("Testaurant");
  }

  @Test
  void findById_shouldReturnMenuItem() {
    Optional<MenuItemEntity> found = menuItemRepo.findById(item.getId());
    assertThat(found).isPresent();
    assertThat(found.get().getPrice()).isEqualTo(30.0);
  }

  @Test
  void findAll_shouldReturnListOfItems() {
    List<MenuItemEntity> item = menuItemRepo.findAll();
    assertThat(item).isNotEmpty();
    assertThat(item.get(0).getName()).isEqualTo("Idli");
  }

  @Test
  void deleteById_shouldRemoveMenuItem() {
    menuItemRepo.deleteById(item.getId());
    Optional<MenuItemEntity> found = menuItemRepo.findById(item.getId());
    assertThat(found).isNotPresent();
  }
}
