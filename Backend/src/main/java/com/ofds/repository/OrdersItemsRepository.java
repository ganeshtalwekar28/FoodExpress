package com.ofds.repository;

import com.ofds.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing persistence operations (CRUD) for the OrderItemEntity, 
 * providing basic database interaction methods inherited from JpaRepository.
 */
@Repository
public interface OrdersItemsRepository extends JpaRepository<OrderItemEntity, Long> {
	
}