package com.ofds.repository;

import com.ofds.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersItemsRepository extends JpaRepository<OrderItemEntity, Long> {
	
}
