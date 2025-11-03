package com.ofds.controller;
 
import com.ofds.dto.MenuItemDTO;
import com.ofds.entity.RestaurantEntity;
import com.ofds.service.MenuItemService;
import com.ofds.service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
 
import java.util.List;
 
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
 
@WebMvcTest(MenuItemController.class)
class MenuItemControllerTest {
 
  @Autowired
  private MockMvc mockMvc;
 
  @SuppressWarnings("removal")
  @MockBean
  private MenuItemService menuItemService;
 
  @SuppressWarnings("removal")
  @MockBean
  private RestaurantService restaurantService;
 
  private MenuItemDTO sampleItem;
  private RestaurantEntity sampleRestaurant;
 
  @BeforeEach
  void setup() {
    sampleItem = new MenuItemDTO();
    sampleItem.setId(1L);
    sampleItem.setName("Paneer Tikka");
    sampleItem.setPrice(150.0);
    sampleItem.setRestaurantId(101L);
 
    sampleRestaurant = new RestaurantEntity();
    sampleRestaurant.setId(1L);
    sampleRestaurant.setName("Spice Hub");
  }
 
  @Test
  void testGetMenuItemsByRestaurantId() throws Exception {
    when(menuItemService.getMenuItemsByRestaurantId(1L)).thenReturn(ResponseEntity.ok(List.of(sampleItem)));
 
    mockMvc.perform(get("/api/menu-items/getMenuItemsByRestaurantId/restaurant/1"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].name").value("Paneer Tikka"));
  }
 
  @Test
  void testCreateMenuItem() throws Exception {
    when(menuItemService.createMenuItem(eq(1L), any(MenuItemDTO.class))).thenReturn(ResponseEntity.ok(sampleItem));
 
    mockMvc.perform(post("/api/menu-items/createMenuItem/restaurant/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"name\":\"Paneer Tikka\",\"price\":150.0,\"externalItemId\":101}"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.name").value("Paneer Tikka"));
  }
 
  @Test
  void testUpdateMenuItem() throws Exception {
    when(menuItemService.updateMenuItem(eq(1L), any(MenuItemDTO.class))).thenReturn(ResponseEntity.ok(sampleItem));
 
    mockMvc.perform(put("/api/menu-items/updateMenuItem/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"name\":\"Paneer Tikka\",\"price\":150.0,\"externalItemId\":101}"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.price").value(150.0));
  }
 
  @Test
  void testDeleteMenuItem() throws Exception {
    when(menuItemService.deleteMenuItem(1L)).thenReturn(ResponseEntity.noContent().build());
 
    mockMvc.perform(delete("/api/menu-items/deleteMenuItem/1"))
      .andExpect(status().isNoContent());
  }
 

}