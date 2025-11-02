package com.ofds.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ofds.entity.RestaurantEntity;

public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Integer> {
	
	
	Optional<RestaurantEntity> findById(Integer Id);
	
	Optional<RestaurantEntity> findByEmailAndPassword(String email, String password);
	
}