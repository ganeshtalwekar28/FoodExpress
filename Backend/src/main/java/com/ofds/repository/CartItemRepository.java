package com.ofds.repository;

import com.ofds.entity.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing persistence operations (CRUD) for the CartItemEntity.
 * It provides basic database interaction methods inherited from JpaRepository.
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {

}