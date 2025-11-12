package com.ofds.controller;
 
import com.ofds.config.JwtUtils;
import com.ofds.dto.MenuItemDTO;
import com.ofds.entity.RestaurantEntity;
import com.ofds.service.MenuItemService;
import com.ofds.service.RestaurantService;
import com.ofds.service.CustomerService;
import com.ofds.service.CustomerUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
 
import java.util.List;
 
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
 
@WebMvcTest(MenuItemController.class)
class MenuItemControllerTest {
	@Autowired
	private MockMvc mockMvc;
 
	@MockBean
	private MenuItemService menuItemService;
 
	@MockBean
	private RestaurantService restaurantService;
  
	@MockBean
	private JwtUtils jwtUtils; 
  
	@MockBean
	private CustomerUserDetailsService customerUserDetailsService; 
  
	@MockBean 
	private CustomerService customerService; 
 
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
	@WithMockUser
	void testGetMenuItemsByRestaurantId() throws Exception {
		when(menuItemService.getMenuItemsByRestaurantId(1L)).thenReturn(ResponseEntity.ok(List.of(sampleItem)));
 
		mockMvc.perform(get("/api/auth/menu-items/getMenuItemsByRestaurantId/restaurant/1"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$[0].name").value("Paneer Tikka"));
	}
 
	@Test
	@WithMockUser(roles = "RESTAURANT_OWNER") 
	void testCreateMenuItem() throws Exception {
		when(menuItemService.createMenuItem(eq(1L), any(MenuItemDTO.class))).thenReturn(ResponseEntity.ok(sampleItem));

		mockMvc.perform(post("/api/auth/menu-items/createMenuItem/restaurant/1")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Paneer Tikka\",\"price\":150.0,\"externalItemId\":101}"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.name").value("Paneer Tikka"));
	}

	@Test
	@WithMockUser(roles = "RESTAURANT_OWNER")
	void testUpdateMenuItem() throws Exception {
		when(menuItemService.updateMenuItem(eq(1L), any(MenuItemDTO.class))).thenReturn(ResponseEntity.ok(sampleItem));

		mockMvc.perform(put("/api/auth/menu-items/updateMenuItem/1")
				.with(csrf()) 
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Paneer Tikka\",\"price\":150.0,\"externalItemId\":101}"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.price").value(150.0));
	}

	@Test
	@WithMockUser(roles = "RESTAURANT_OWNER")
	void testDeleteMenuItem() throws Exception {
		when(menuItemService.deleteMenuItem(1L)).thenReturn(ResponseEntity.noContent().build());

		mockMvc.perform(delete("/api/auth/menu-items/deleteMenuItem/1")
				.with(csrf())) 
		.andExpect(status().isNoContent());
	}
}