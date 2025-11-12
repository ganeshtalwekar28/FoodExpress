package com.ofds.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ofds.entity.MenuItemEntity;

/**
 * Repository interface for managing persistence operations (CRUD) for the MenuItemEntity, 
 * including methods for searching and retrieval based on menu item name.
 */
@Repository
public interface MenuItemRepository extends JpaRepository<MenuItemEntity, Long> {

    /**
     * Finds and returns a specific MenuItemEntity based on its primary ID.
     */
	Optional<MenuItemEntity> findById(Long id);
	
    /**
     * Finds a list of MenuItemEntity objects whose name contains the specified string, 
     * performing a case-insensitive search.
     */
	List<MenuItemEntity> findByNameContainingIgnoreCase(String name);
}