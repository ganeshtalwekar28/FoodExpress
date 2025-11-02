package com.ofds.service;

import com.ofds.dto.MenuItemDTO;
import com.ofds.entity.MenuItemEntity;
import com.ofds.entity.RestaurantEntity;
import com.ofds.exception.DataNotFoundException;
import com.ofds.repository.MenuItemRepository;
import com.ofds.repository.RestaurantRepository;
import com.ofds.service.MenuItemService;

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
	restaurant.setId(1);
	restaurant.setName("Testaurant");
	menuItemEntity = new MenuItemEntity();
	menuItemEntity.setId(10);
	menuItemEntity.setName("Paneer Tikka");
	menuItemEntity.setPrice(150.0);
	menuItemEntity.setRestaurant(restaurant);

		menuItemDTO = new MenuItemDTO();
		menuItemDTO.setId(10);
		menuItemDTO.setName("Paneer Tikka");
		menuItemDTO.setPrice(150.0);
		menuItemDTO.setRestaurantId(1001);
	}

	@Test
	void getMenuItemsByRestaurantId_returnsList_whenRestaurantExists() throws Exception {
		restaurant.setMenuItems(List.of(menuItemEntity));
		when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurant));
		when(modelMapper.map(menuItemEntity, MenuItemDTO.class)).thenReturn(menuItemDTO);

		ResponseEntity<List<MenuItemDTO>> resp = service.getMenuItemsByRestaurantId(1);
		assertThat(resp.getStatusCodeValue()).isEqualTo(200);
		assertThat(resp.getBody()).isNotNull();
		assertThat(resp.getBody()).hasSize(1);
		assertThat(resp.getBody().get(0).getName()).isEqualTo("Paneer Tikka");
		verify(restaurantRepository).findById(1);
		verify(modelMapper).map(menuItemEntity, MenuItemDTO.class);
	}

	@Test
	void getMenuItemsByRestaurantId_throws_whenRestaurantNotFound() {
		when(restaurantRepository.findById(99)).thenReturn(Optional.empty());
		DataNotFoundException ex = assertThrows(DataNotFoundException.class, () ->
		service.getMenuItemsByRestaurantId(99));
		assertThat(ex.getMessage()).contains("Restaurant not found with id: 99");
		verify(restaurantRepository).findById(99);
	}

	@Test
	void createMenuItem_createsAndReturnsDto_whenRestaurantExists() throws Exception {
		when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurant));
		// map dto->entity
		when(modelMapper.map(menuItemDTO, MenuItemEntity.class)).thenReturn(menuItemEntity);
		
		// repository save returns entity with id (simulate)
		MenuItemEntity savedEntity = new MenuItemEntity();
		savedEntity.setId(11);
		savedEntity.setName(menuItemEntity.getName());
		savedEntity.setPrice(menuItemEntity.getPrice());
		savedEntity.setRestaurant(restaurant);
		when(menuItemRepository.save(menuItemEntity)).thenReturn(savedEntity);

		// map entity->dto
		MenuItemDTO returnedDto = new MenuItemDTO();
		returnedDto.setId(11);
		returnedDto.setName(savedEntity.getName());
		returnedDto.setPrice(savedEntity.getPrice());
		when(modelMapper.map(savedEntity, MenuItemDTO.class)).thenReturn(returnedDto);
		
		ResponseEntity<MenuItemDTO> resp = service.createMenuItem(1, menuItemDTO);
		assertThat(resp.getStatusCodeValue()).isEqualTo(201);
		assertThat(resp.getBody()).isNotNull();
		assertThat(resp.getBody().getId()).isEqualTo(11);
		assertThat(resp.getBody().getRestaurantId()).isEqualTo(1);

		verify(restaurantRepository).findById(1);
		verify(menuItemRepository).save(menuItemEntity);
		verify(modelMapper).map(menuItemDTO, MenuItemEntity.class);
		verify(modelMapper).map(savedEntity, MenuItemDTO.class);
	}

	@Test
	void createMenuItem_throws_whenRestaurantNotFound() {
		when(restaurantRepository.findById(5)).thenReturn(Optional.empty());

		DataNotFoundException ex = assertThrows(DataNotFoundException.class, () ->
		service.createMenuItem(5, menuItemDTO));

		assertThat(ex.getMessage()).contains("Restaurant not found with id: 5");
		verify(restaurantRepository).findById(5);
		verifyNoInteractions(menuItemRepository);
	}

	@Test
	void updateMenuItem_updatesAndReturnsDto_whenItemExists() throws Exception {
		MenuItemEntity existing = new MenuItemEntity();
		existing.setId(20);
		existing.setName("Old Name");
		existing.setPrice(100.0);
		existing.setRestaurant(restaurant);
		when(menuItemRepository.findById(20)).thenReturn(Optional.of(existing));

		// DTO contains new values
		MenuItemDTO updateDto = new MenuItemDTO();
		updateDto.setName("New Name");
		updateDto.setPrice(200.0);
		updateDto.setImage_url("img.png");

		// repository.save returns updated entity
		MenuItemEntity updatedEntity = new MenuItemEntity();
		updatedEntity.setId(20);
		updatedEntity.setName("New Name");
		updatedEntity.setPrice(200.0);
		updatedEntity.setImage_url("img.png");
		updatedEntity.setRestaurant(restaurant);

		when(menuItemRepository.save(existing)).thenReturn(updatedEntity);
		when(modelMapper.map(updatedEntity, MenuItemDTO.class)).thenReturn(updateDto);

		ResponseEntity<MenuItemDTO> resp = service.updateMenuItem(20, updateDto);
		assertThat(resp.getStatusCodeValue()).isEqualTo(200);
		assertThat(resp.getBody()).isNotNull();
		assertThat(resp.getBody().getName()).isEqualTo("New Name");
		assertThat(resp.getBody().getPrice()).isEqualTo(200.0);

		verify(menuItemRepository).findById(20);
		verify(menuItemRepository).save(existing);
		verify(modelMapper).map(updatedEntity, MenuItemDTO.class);
	}

	@Test

	void updateMenuItem_throws_whenNotFound() {

		when(menuItemRepository.findById(999)).thenReturn(Optional.empty());

		DataNotFoundException ex = assertThrows(DataNotFoundException.class, () ->

		service.updateMenuItem(999, menuItemDTO)

		);

		assertThat(ex.getMessage()).contains("Menu item not found with id: 999");

		verify(menuItemRepository).findById(999);

		verifyNoMoreInteractions(menuItemRepository);

	}

	@Test

	void deleteMenuItem_deletes_whenExists() throws Exception {

		when(menuItemRepository.findById(30)).thenReturn(Optional.of(menuItemEntity));

		ResponseEntity<Void> resp = service.deleteMenuItem(30);

		assertThat(resp.getStatusCodeValue()).isEqualTo(200);

		verify(menuItemRepository).findById(30);

		verify(menuItemRepository).deleteById(30);

	}

	@Test

	void deleteMenuItem_throws_whenNotFound() {

		when(menuItemRepository.findById(400)).thenReturn(Optional.empty());

		DataNotFoundException ex = assertThrows(DataNotFoundException.class, () ->

		service.deleteMenuItem(400)

		);

		assertThat(ex.getMessage()).contains("Menu item not found with id: 400");

		verify(menuItemRepository).findById(400);

		verify(menuItemRepository, never()).deleteById(anyInt());

	}
//
//	@Test
//
//	void getRestaurantByEmail_returnsList() {
//
//		RestaurantEntity r1 = new RestaurantEntity();
//
//		r1.setId(5);
//
//		r1.setName("R1");
//
//		when(restaurantRepository.findByEmail("owner@example.com")).thenReturn(List.of(r1));
//
//		List<RestaurantEntity> res = service.getRestaurantByEmail("owner@example.com");
//
//		assertThat(res).hasSize(1);
//
//		assertThat(res.get(0).getName()).isEqualTo("R1");
//
//		verify(restaurantRepository).findByEmail("owner@example.com");
//
//	}

}
