package com.ofds.repository;

import com.ofds.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    
    // Custom derived query method to fetch orders for a specific user, sorted by date.
    // NOTE: This assumes OrderEntity has a field named 'user' which has a property 'id'.
    // If OrderEntity uses 'user_id' directly (primitive), the method name changes slightly.
    List<OrderEntity> findByUserIdOrderByOrderDateDesc(Integer userId);
    
    // If OrderEntity uses 'user' (object) and the ID is an Integer:
    // List<OrderEntity> findByUser_IdOrderByOrderDateDesc(Integer userId);
    // Based on your OrderEntity, the first option (findByUserId...) is the most common and likely correct approach.
}