package com.ofds.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ofds.entity.RestaurantEntity;
import java.util.List;


public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Long> {
	
	Optional<RestaurantEntity> findById(Long Id);
		
	Optional<RestaurantEntity> findByEmailAndPassword(String email, String password);
	
}