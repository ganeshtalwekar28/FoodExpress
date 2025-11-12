package com.ofds.repository;

import com.ofds.entity.CartEntity;
import com.ofds.entity.CustomerEntity;
import com.ofds.entity.RestaurantEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CartRepositoryTest.java
 * Tests the custom JpaRepository method: findByCustomer(CustomerEntity customer)
 */
// FIX: Use @ContextConfiguration to load the main application class.
// This forces the test environment to use the application's default
// configuration for finding ALL entities (Customer, Cart, Order, OrderItem, etc.)
@SpringBootTest
@ActiveProfiles("test") // Ensures H2 is active if configured in application-test.properties/yaml
@TestPropertySource(properties = {
    // Override ddl-auto just in case
    "spring.jpa.hibernate.ddl-auto=create-drop" 
})
class CartRepositoryTest {
    
    // Inject all required repositories
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    // -------------------------------------------------------------------------

    @Test
    void testFindByCustomer_ShouldReturnCartWhenFound() {
        // 1. Arrange: Create and save all dependent entities.
        
        // Create Customer
        CustomerEntity customer = new CustomerEntity();
        customer.setName("Test Customer");
        customer.setEmail("test@customer.com");
        customer.setPhone("1234567890"); 
        customer.setPassword("securepass123"); 
        customer.setTermsAccepted(true);
        customer = customerRepository.save(customer); 
        
        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setName("Test Restaurant");
        restaurant.setEmail("rest@test.com");
        restaurant.setPhone("9876543210"); 
        restaurant.setAddress("456 Test Lane");
        restaurant = restaurantRepository.save(restaurant);

        CartEntity cart = new CartEntity();
        cart.setCustomer(customer);
        cart.setRestaurant(restaurant);
        cart.setItemCount(1);
        cart.setTotalAmount(50.0);
        cart = cartRepository.save(cart);

        Optional<CartEntity> result = cartRepository.findByCustomer(customer);

        assertTrue(result.isPresent(), "The cart should be found for the given customer.");
        
        CartEntity foundCart = result.get();
        assertEquals(cart.getId(), foundCart.getId());
        assertEquals(customer.getId(), foundCart.getCustomer().getId());
    }
    
    @Test
    void testFindByCustomer_ShouldReturnEmptyWhenCartNotFound() {
        CustomerEntity customerWithoutCart = new CustomerEntity();
        customerWithoutCart.setName("No Cart User");
        customerWithoutCart.setEmail("nocart@test.com");
        customerWithoutCart.setPhone("1111111111"); 
        customerWithoutCart.setPassword("pass"); 
        customerWithoutCart.setTermsAccepted(false);
        customerWithoutCart = customerRepository.save(customerWithoutCart); 

        Optional<CartEntity> result = cartRepository.findByCustomer(customerWithoutCart);

        assertFalse(result.isPresent(), "No cart should be found for a customer who doesn't have one.");
    }
}