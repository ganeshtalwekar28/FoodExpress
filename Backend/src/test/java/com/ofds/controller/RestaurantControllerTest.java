package com.ofds.controller;

import com.ofds.dto.RestaurantDTO;
import com.ofds.entity.RestaurantEntity;
import com.ofds.exception.DataNotFoundException;
import com.ofds.mapper.RestaurantMapper;
import com.ofds.service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestaurantControllerTest {

    @InjectMocks
    private RestaurantController restaurantController;

    @Mock
    private RestaurantService restaurantService;

    @Mock
    private RestaurantMapper restaurantMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllRestaurants() throws DataNotFoundException {
        RestaurantEntity entity = new RestaurantEntity();
        entity.setId(1L);
        entity.setName("Test Restaurant");

        RestaurantDTO dto = new RestaurantDTO();
        dto.setId(1L);
        dto.setName("Test Restaurant");

        when(restaurantService.getAllRestaurants()).thenReturn(List.of(entity));
        when(restaurantMapper.toDTO(entity)).thenReturn(dto);

        ResponseEntity<List<RestaurantDTO>> response = restaurantController.getAllRestaurants();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Restaurant", response.getBody().get(0).getName());
    }

    @Test
    void testGetRestaurantByEmailAndPassword() throws DataNotFoundException {
        RestaurantEntity entity = new RestaurantEntity();
        entity.setId(2L);
        entity.setEmail("test@example.com");
        entity.setPassword("secret");

        when(restaurantService.findByEmailAndPassword("test@example.com", "secret")).thenReturn(entity);

        ResponseEntity<RestaurantEntity> response = restaurantController.getRestaurantByEmailAndPassword("test@example.com", "secret");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("test@example.com", response.getBody().getEmail());
    }

    @Test
    void testCreateRestaurant() {
        RestaurantEntity input = new RestaurantEntity();
        input.setName("New Restaurant");

        RestaurantEntity saved = new RestaurantEntity();
        saved.setId(3L);
        saved.setName("New Restaurant");

        when(restaurantService.createRestaurant(input)).thenReturn(saved);

        ResponseEntity<RestaurantEntity> response = restaurantController.createRestaurant(input);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(3, response.getBody().getId());
    }

    @Test
    void testDeleteRestaurant() throws DataNotFoundException {
        Long id = 4L;
        ResponseEntity<Void> expected = ResponseEntity.noContent().build();

        when(restaurantService.deleteRestaurant(id)).thenReturn(expected);

        ResponseEntity<Void> response = restaurantController.deleteRestaurant(id);

        assertEquals(204, response.getStatusCodeValue());
        verify(restaurantService, times(1)).deleteRestaurant(id);
    }
}
