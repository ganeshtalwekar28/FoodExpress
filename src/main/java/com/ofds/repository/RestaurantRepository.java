package com.ofds.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ofds.entity.RestaurantEntity;

/**
 * Repository interface for managing persistence operations (CRUD) for the RestaurantEntity.
 * It includes custom methods for retrieval by ID and for login verification.
 */
@Repository
public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Long> {
	
    /**
     * Finds and returns a specific RestaurantEntity based on its primary ID.
     */
	Optional<RestaurantEntity> findById(Long id);
		
    /**
     * Finds a RestaurantEntity based on the provided email and password, typically used for owner login verification.
     */
	Optional<RestaurantEntity> findByEmailAndPassword(String email, String password);
}