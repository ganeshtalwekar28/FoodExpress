package com.ofds.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ofds.entity.MenuItemEntity;

public interface MenuItemRepository extends JpaRepository<MenuItemEntity, Long> {

	Optional<MenuItemEntity> findById(Long id);
	
	List<MenuItemEntity> findByNameContainingIgnoreCase(String name);
}
