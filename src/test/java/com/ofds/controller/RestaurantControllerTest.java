////package com.ofds.controller;
////
////import com.ofds.entity.RestaurantEntity;
////import com.ofds.service.RestaurantService;
////import org.junit.jupiter.api.BeforeEach;
////import org.junit.jupiter.api.Test;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
////import org.springframework.boot.test.mock.mockito.MockBean;
////import org.springframework.http.MediaType;
////import org.springframework.http.ResponseEntity;
////import org.springframework.test.web.servlet.MockMvc;
////
////import java.util.List;
////
////import static org.mockito.ArgumentMatchers.any;
////import static org.mockito.ArgumentMatchers.eq;
////import static org.mockito.Mockito.when;
////import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
////import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
////
////@WebMvcTest(RestaurantController.class)
////class RestaurantControllerTest {
////
////    @Autowired
////    private MockMvc mockMvc;
////
////    @MockBean
////    private RestaurantService restaurantService;
////
////    private RestaurantEntity sampleRestaurant;
////
////    @BeforeEach
////    void setup() {
////        sampleRestaurant = new RestaurantEntity();
////        sampleRestaurant.setId(1);
////        sampleRestaurant.setName("Spice Hub");
////        sampleRestaurant.setEmail("owner@example.com");
////        sampleRestaurant.setPassword("secure123");}
////        
////    @Test
////    void testCreateRestaurant() throws Exception {
////        when(restaurantService.createRestaurant(any(RestaurantEntity.class)))
////                .thenReturn(ResponseEntity.ok(sampleRestaurant));
////
////        mockMvc.perform(post("/api/restaurants/createRestaurant")
////                .contentType(MediaType.APPLICATION_JSON)
////                .content("{\"name\":\"Spice Hub\",\"email\":\"owner@example.com\",\"password\":\"secure123\"}"))
////                .andExpect(status().isOk())
////                .andExpect(jsonPath("$.name").value("Spice Hub"));
////    }
////
////    @Test
////    void testGetAllRestaurants() throws Exception {
////        when(restaurantService.getAllRestaurants())
////                .thenReturn(ResponseEntity.ok(List.of(sampleRestaurant)));
////
////        mockMvc.perform(get("/api/restaurants/getAllRestaurants"))
////                .andExpect(status().isOk())
////                .andExpect(jsonPath("$[0].name").value("Spice Hub"));
////    }
////
////    @Test
////    void testDeleteRestaurant() throws Exception {
////        when(restaurantService.deleteRestaurant(1))
////                .thenReturn(ResponseEntity.noContent().build());
////
////        mockMvc.perform(delete("/api/restaurants/deleteRestaurant/1"))
////                .andExpect(status().isNoContent());
////    }
////
////    @Test
////    void testGetRestaurantByEmailAndPassword() throws Exception {
////        when(restaurantService.findByEmailAndPassword("owner@example.com", "secure123"))
////                .thenReturn(sampleRestaurant);
////
////        mockMvc.perform(get("/api/restaurants/owner@example.com/secure123"))
////                .andExpect(status().isOk())
////                .andExpect(jsonPath("$.name").value("Spice Hub"));
////    }
////}
//
//package com.ofds.controller;
//
//import com.ofds.dto.RestaurantDTO;
//import com.ofds.entity.RestaurantEntity;
//import com.ofds.exception.DataNotFoundException;
//import com.ofds.config.RestaurantMapper;
//import com.ofds.service.RestaurantService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//import org.springframework.http.ResponseEntity;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class RestaurantControllerTest {
//
//    @InjectMocks
//    private RestaurantController restaurantController;
//
//    @Mock
//    private RestaurantService restaurantService;
//
//    @Mock
//    private RestaurantMapper restaurantMapper;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testGetAllRestaurants() throws DataNotFoundException {
//        RestaurantEntity entity = new RestaurantEntity();
//        entity.setId(1);
//        entity.setName("Test Restaurant");
//
//        RestaurantDTO dto = new RestaurantDTO();
//        dto.setId(1);
//        dto.setName("Test Restaurant");
//
//        when(restaurantService.getAllRestaurants()).thenReturn(List.of(entity));
//        when(restaurantMapper.toDTO(entity)).thenReturn(dto);
//
//        ResponseEntity<List<RestaurantDTO>> response = restaurantController.getAllRestaurants();
//
//        assertEquals(200, response.getStatusCodeValue());
//        assertEquals(1, response.getBody().size());
//        assertEquals("Test Restaurant", response.getBody().get(0).getName());
//    }
//
//    @Test
//    void testGetRestaurantByEmailAndPassword() {
//        RestaurantEntity entity = new RestaurantEntity();
//        entity.setId(2);
//        entity.setEmail("test@example.com");
//        entity.setPassword("secret");
//
//        when(restaurantService.findByEmailAndPassword("test@example.com", "secret")).thenReturn(entity);
//
//        ResponseEntity<RestaurantEntity> response = restaurantController.getRestaurantByEmailAndPassword("test@example.com", "secret");
//
//        assertEquals(200, response.getStatusCodeValue());
//        assertEquals("test@example.com", response.getBody().getEmail());
//    }
//
//    @Test
//    void testCreateRestaurant() {
//        RestaurantEntity input = new RestaurantEntity();
//        input.setName("New Restaurant");
//
//        RestaurantEntity saved = new RestaurantEntity();
//        saved.setId(3);
//        saved.setName("New Restaurant");
//
//        when(restaurantService.createRestaurant(input)).thenReturn(saved);
//
//        ResponseEntity<RestaurantEntity> response = restaurantController.createRestaurant(input);
//
//        assertEquals(201, response.getStatusCodeValue());
//        assertEquals(3, response.getBody().getId());
//    }
//
//    @Test
//    void testDeleteRestaurant() throws DataNotFoundException {
//        Integer id = 4;
//        ResponseEntity<Void> expected = ResponseEntity.noContent().build();
//
//        when(restaurantService.deleteRestaurant(id)).thenReturn(expected);
//
//        ResponseEntity<Void> response = restaurantController.deleteRestaurant(id);
//
//        assertEquals(204, response.getStatusCodeValue());
//        verify(restaurantService, times(1)).deleteRestaurant(id);
//    }
//}
