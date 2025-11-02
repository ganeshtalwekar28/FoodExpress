package com.ofds.repository;

import com.ofds.entity.CartEntity;
import com.ofds.entity.CustomerEntity;
import com.ofds.entity.RestaurantEntity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    void testFindByCustomer() {
        // Arrange: create and save customer
        CustomerEntity customer = new CustomerEntity();
        customer.setName("John Doe");
        customer.setEmail("john@example.com");

        // Create and save restaurant
        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setName("Testaurant");

        // Create and save cart
        CartEntity cart = new CartEntity();
        cart.setCustomer(customer);
        cart.setRestaurant(restaurant);
        cart.setItemCount(0);
        cart.setTotalAmount(0.0);

        // Act
        Optional<CartEntity> result = cartRepository.findByCustomer(customer);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(customer.getId(), result.get().getCustomer().getId());
    }
}
