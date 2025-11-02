package com.ofds.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ofds.entity.MenuItemEntity;

public interface MenuItemRepository extends JpaRepository<MenuItemEntity, Integer> {

	Optional<MenuItemEntity> findById(Integer id);
	
	List<MenuItemEntity> findByNameContainingIgnoreCase(String name);
}
