package com.ofds.service;

import com.ofds.dto.MenuItemDTO;
import com.ofds.entity.MenuItemEntity;
import com.ofds.entity.RestaurantEntity;
import com.ofds.exception.DataNotFoundException;
import com.ofds.repository.MenuItemRepository;
import com.ofds.repository.RestaurantRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class MenuItemServiceTest {

	@InjectMocks
    private MenuItemService service;

	@Mock
    private MenuItemRepository menuItemRepository;

	@Mock
	private RestaurantRepository restaurantRepository;

	@Mock
	private ModelMapper modelMapper;

	private RestaurantEntity restaurant;
	private MenuItemEntity menuItemEntity;
	private MenuItemDTO menuItemDTO;

	@BeforeEach
	void setUp() {
	restaurant = new RestaurantEntity();
	restaurant.setId(1L);
	restaurant.setName("Testaurant");
	menuItemEntity = new MenuItemEntity();
	menuItemEntity.setId(10L);
	menuItemEntity.setName("Paneer Tikka");
	menuItemEntity.setPrice(150.0);
	menuItemEntity.setRestaurant(restaurant);

		menuItemDTO = new MenuItemDTO();
		menuItemDTO.setId(10L);
		menuItemDTO.setName("Paneer Tikka");
		menuItemDTO.setPrice(150.0);
		menuItemDTO.setRestaurantId(1001L);
	}

	@SuppressWarnings("deprecation")
	@Test
	void getMenuItemsByRestaurantId_returnsList_whenRestaurantExists() throws Exception {
		restaurant.setMenuItems(List.of(menuItemEntity));
		when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
		when(modelMapper.map(menuItemEntity, MenuItemDTO.class)).thenReturn(menuItemDTO);

		ResponseEntity<List<MenuItemDTO>> resp = service.getMenuItemsByRestaurantId(1L);
		assertThat(resp.getStatusCodeValue()).isEqualTo(200);
		assertThat(resp.getBody()).isNotNull();
		assertThat(resp.getBody()).hasSize(1);
		assertThat(resp.getBody().get(0).getName()).isEqualTo("Paneer Tikka");
		verify(restaurantRepository).findById(1L);
		verify(modelMapper).map(menuItemEntity, MenuItemDTO.class);
	}

	@Test
	void getMenuItemsByRestaurantId_throws_whenRestaurantNotFound() {
		when(restaurantRepository.findById(99L)).thenReturn(Optional.empty());
		DataNotFoundException ex = assertThrows(DataNotFoundException.class, () ->
		service.getMenuItemsByRestaurantId(99L));
		assertThat(ex.getMessage()).contains("Restaurant not found with id: 99");
		verify(restaurantRepository).findById(99L);
	}

	@Test
	void createMenuItem_createsAndReturnsDto_whenRestaurantExists() throws Exception {
		when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
		// map dto->entity
		when(modelMapper.map(menuItemDTO, MenuItemEntity.class)).thenReturn(menuItemEntity);
		
		// repository save returns entity with id (simulate)
		MenuItemEntity savedEntity = new MenuItemEntity();
		savedEntity.setId(11L);
		savedEntity.setName(menuItemEntity.getName());
		savedEntity.setPrice(menuItemEntity.getPrice());
		savedEntity.setRestaurant(restaurant);
		when(menuItemRepository.save(menuItemEntity)).thenReturn(savedEntity);

		// map entity->dto
		MenuItemDTO returnedDto = new MenuItemDTO();
		returnedDto.setId(11L);
		returnedDto.setName(savedEntity.getName());
		returnedDto.setPrice(savedEntity.getPrice());
		when(modelMapper.map(savedEntity, MenuItemDTO.class)).thenReturn(returnedDto);
		
		ResponseEntity<MenuItemDTO> resp = service.createMenuItem(1L, menuItemDTO);
		assertThat(resp.getStatusCodeValue()).isEqualTo(201);
		assertThat(resp.getBody()).isNotNull();
		assertThat(resp.getBody().getId()).isEqualTo(11);
		assertThat(resp.getBody().getRestaurantId()).isEqualTo(1);

		verify(restaurantRepository).findById(1L);
		verify(menuItemRepository).save(menuItemEntity);
		verify(modelMapper).map(menuItemDTO, MenuItemEntity.class);
		verify(modelMapper).map(savedEntity, MenuItemDTO.class);
	}

	@Test
	void createMenuItem_throws_whenRestaurantNotFound() {
		when(restaurantRepository.findById(5L)).thenReturn(Optional.empty());

		DataNotFoundException ex = assertThrows(DataNotFoundException.class, () ->
		service.createMenuItem(5L, menuItemDTO));

		assertThat(ex.getMessage()).contains("Restaurant not found with id: 5");
		verify(restaurantRepository).findById(5L);
		verifyNoInteractions(menuItemRepository);
	}

	@Test
	void updateMenuItem_updatesAndReturnsDto_whenItemExists() throws Exception {
		MenuItemEntity existing = new MenuItemEntity();
		existing.setId(20L);
		existing.setName("Old Name");
		existing.setPrice(100.0);
		existing.setRestaurant(restaurant);
		when(menuItemRepository.findById(20L)).thenReturn(Optional.of(existing));

		// DTO contains new values
		MenuItemDTO updateDto = new MenuItemDTO();
		updateDto.setName("New Name");
		updateDto.setPrice(200.0);
		updateDto.setImage_url("img.png");

		// repository.save returns updated entity
		MenuItemEntity updatedEntity = new MenuItemEntity();
		updatedEntity.setId(20L);
		updatedEntity.setName("New Name");
		updatedEntity.setPrice(200.0);
		updatedEntity.setImage_url("img.png");
		updatedEntity.setRestaurant(restaurant);

		when(menuItemRepository.save(existing)).thenReturn(updatedEntity);
		when(modelMapper.map(updatedEntity, MenuItemDTO.class)).thenReturn(updateDto);

		ResponseEntity<MenuItemDTO> resp = service.updateMenuItem(20L, updateDto);
		assertThat(resp.getStatusCodeValue()).isEqualTo(200);
		assertThat(resp.getBody()).isNotNull();
		assertThat(resp.getBody().getName()).isEqualTo("New Name");
		assertThat(resp.getBody().getPrice()).isEqualTo(200.0);

		verify(menuItemRepository).findById(20L);
		verify(menuItemRepository).save(existing);
		verify(modelMapper).map(updatedEntity, MenuItemDTO.class);
	}

	@Test
	void updateMenuItem_throws_whenNotFound() {
		when(menuItemRepository.findById(999L)).thenReturn(Optional.empty());
		DataNotFoundException ex = assertThrows(DataNotFoundException.class, () ->
		service.updateMenuItem(999L, menuItemDTO)
		);

		assertThat(ex.getMessage()).contains("Menu item not found with id: 999");
		verify(menuItemRepository).findById(999L);
		verifyNoMoreInteractions(menuItemRepository);
	}

	@Test
	void deleteMenuItem_deletes_whenExists() throws Exception {
		when(menuItemRepository.findById(30L)).thenReturn(Optional.of(menuItemEntity));
		ResponseEntity<Void> resp = service.deleteMenuItem(30L);
		assertThat(resp.getStatusCodeValue()).isEqualTo(200);
		verify(menuItemRepository).findById(30L);
		verify(menuItemRepository).deleteById(30L);
	}

	@Test
	void deleteMenuItem_throws_whenNotFound() {
		when(menuItemRepository.findById(400L)).thenReturn(Optional.empty());
		DataNotFoundException ex = assertThrows(DataNotFoundException.class, () ->
		service.deleteMenuItem(400L)
		);
		
		assertThat(ex.getMessage()).contains("Menu item not found with id: 400");
		verify(menuItemRepository).findById(400L);
		verify(menuItemRepository, never()).deleteById(anyLong());
	}
}
