package com.ofds.service;

import com.ofds.entity.RestaurantEntity;
import com.ofds.exception.DataNotFoundException;
import com.ofds.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestaurantServiceTest {

    @InjectMocks
    private RestaurantService restaurantService;

    @Mock
    private RestaurantRepository restaurantRepo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateRestaurant() {
        RestaurantEntity input = new RestaurantEntity();
        input.setName("Test Restaurant");

        RestaurantEntity saved = new RestaurantEntity();
        saved.setId(1L);
        saved.setName("Test Restaurant");

        when(restaurantRepo.save(input)).thenReturn(saved);

        RestaurantEntity result = restaurantService.createRestaurant(input);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Test Restaurant", result.getName());
    }

    @Test
    void testGetAllRestaurants_success() throws DataNotFoundException {
        RestaurantEntity r1 = new RestaurantEntity();
        r1.setId(1L);
        r1.setName("R1");

        when(restaurantRepo.findAll()).thenReturn(List.of(r1));

        List<RestaurantEntity> result = restaurantService.getAllRestaurants();

        assertEquals(1, result.size());
        assertEquals("R1", result.get(0).getName());
    }

    @Test
    void testGetAllRestaurants_emptyList_throwsException() {
        when(restaurantRepo.findAll()).thenReturn(List.of());

        assertThrows(DataNotFoundException.class, () -> restaurantService.getAllRestaurants());
    }

    @Test
    void testDeleteRestaurant_success() throws DataNotFoundException {
        RestaurantEntity r = new RestaurantEntity();
        r.setId(5L);

        when(restaurantRepo.findById(5L)).thenReturn(Optional.of(r));

        ResponseEntity<Void> response = restaurantService.deleteRestaurant(5L);

        assertEquals(204, response.getStatusCodeValue());
        verify(restaurantRepo, times(1)).deleteById(5L);
    }

    @Test
    void testDeleteRestaurant_notFound_throwsException() {
        when(restaurantRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> restaurantService.deleteRestaurant(99L));
    }

    @Test
    void testFindByEmailAndPassword_success() {
        RestaurantEntity r = new RestaurantEntity();
        r.setEmail("owner@example.com");
        r.setPassword("secret");

        when(restaurantRepo.findByEmailAndPassword("owner@example.com", "secret"))
                .thenReturn(Optional.of(r));

        RestaurantEntity result = restaurantService.findByEmailAndPassword("owner@example.com", "secret");

        assertNotNull(result);
        assertEquals("owner@example.com", result.getEmail());
    }

    @Test
    void testFindByEmailAndPassword_notFound_throwsException() {
        when(restaurantRepo.findByEmailAndPassword("x@y.com", "wrong"))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
                restaurantService.findByEmailAndPassword("x@y.com", "wrong"));
    }
}
