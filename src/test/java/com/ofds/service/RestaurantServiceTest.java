//package com.ofds.service;
// 
//import com.ofds.entity.RestaurantEntity;
//import com.ofds.exception.DataNotFoundException;
//import com.ofds.repository.RestaurantRepository;
//import com.ofds.service.RestaurantService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.*;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.ResponseEntity;
// 
//import java.util.List;
//import java.util.Optional;
// 
//import static org.assertj.core.api.Assertions.*;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
// 
//@ExtendWith(MockitoExtension.class)
//class RestaurantServiceTest {
// 
//  @InjectMocks
//  private RestaurantService service;
// 
//  @Mock
//  private RestaurantRepository restaurantRepo;
// 
//  private RestaurantEntity sample;
// 
//  @BeforeEach
//  void setUp() {
//    sample = new RestaurantEntity();
//    sample.setId(1);
//    sample.setName("Testaurant");
//    sample.setEmail("owner@example.com");
//    sample.setPassword("secret");
//  }
// 
//  @Test
//  void createRestaurant_returnsCreatedEntity() {
//    when(restaurantRepo.save(sample)).thenReturn(sample);
// 
//    ResponseEntity<RestaurantEntity> resp = service.createRestaurant(sample);
// 
//    assertThat(resp.getStatusCodeValue()).isEqualTo(201);
//    assertThat(resp.getBody()).isEqualTo(sample);
//    verify(restaurantRepo).save(sample);
//  }
// 
//  @Test
//  void getAllRestaurants_returnsList_whenNotEmpty() throws Exception {
//    when(restaurantRepo.findAll()).thenReturn(List.of(sample));
// 
//    ResponseEntity<List<RestaurantEntity>> resp = service.getAllRestaurants();
// 
//    assertThat(resp.getStatusCodeValue()).isEqualTo(200);
//    assertThat(resp.getBody()).isNotNull();
//    assertThat(resp.getBody()).hasSize(1);
//    assertThat(resp.getBody().get(0).getName()).isEqualTo("Testaurant");
//    verify(restaurantRepo).findAll();
//  }
// 
//  @Test
//  void getAllRestaurants_throwsDataNotFoundException_whenEmpty() {
//    when(restaurantRepo.findAll()).thenReturn(List.of());
// 
//    DataNotFoundException ex = assertThrows(DataNotFoundException.class, () -> service.getAllRestaurants());
// 
//    assertThat(ex.getMessage()).contains("No restaurants found");
//    verify(restaurantRepo).findAll();
//  }
// 
//  @Test
//  void deleteRestaurant_deletes_whenFound() throws Exception {
//    when(restaurantRepo.findById(1)).thenReturn(Optional.of(sample));
//    doNothing().when(restaurantRepo).deleteById(1);
// 
//    ResponseEntity<Void> resp = service.deleteRestaurant(1);
// 
//    assertThat(resp.getStatusCodeValue()).isEqualTo(204);
//    verify(restaurantRepo).findById(1);
//    verify(restaurantRepo).deleteById(1);
//  }
// 
//  @Test
//  void deleteRestaurant_throwsDataNotFoundException_whenNotFound() {
//    when(restaurantRepo.findById(99)).thenReturn(Optional.empty());
// 
//    DataNotFoundException ex = assertThrows(DataNotFoundException.class, () -> service.deleteRestaurant(99));
// 
//    assertThat(ex.getMessage()).contains("Restaurant not found with id: 99");
//    verify(restaurantRepo).findById(99);
//    verify(restaurantRepo, never()).deleteById(anyInt());
//  }
// 
//  @Test
//  void findByEmailAndPassword_returnsEntity_whenPresent() {
//    when(restaurantRepo.findByEmailAndPassword("owner@example.com", "secret")).thenReturn(Optional.of(sample));
// 
//    RestaurantEntity result = service.findByEmailAndPassword("owner@example.com", "secret");
// 
//    assertThat(result).isEqualTo(sample);
//    verify(restaurantRepo).findByEmailAndPassword("owner@example.com", "secret");
//  }
// 
//  @Test
//  void findByEmailAndPassword_throwsNoSuchElement_whenAbsent() {
//    when(restaurantRepo.findByEmailAndPassword("no@x.com", "bad")).thenReturn(Optional.empty());
// 
//    assertThrows(java.util.NoSuchElementException.class, () ->
//      service.findByEmailAndPassword("no@x.com", "bad")
//    );
// 
//    verify(restaurantRepo).findByEmailAndPassword("no@x.com", "bad");
//  }
//}


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
        saved.setId(1);
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
        r1.setId(1);
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
        r.setId(5);

        when(restaurantRepo.findById(5)).thenReturn(Optional.of(r));

        ResponseEntity<Void> response = restaurantService.deleteRestaurant(5);

        assertEquals(204, response.getStatusCodeValue());
        verify(restaurantRepo, times(1)).deleteById(5);
    }

    @Test
    void testDeleteRestaurant_notFound_throwsException() {
        when(restaurantRepo.findById(99)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> restaurantService.deleteRestaurant(99));
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
