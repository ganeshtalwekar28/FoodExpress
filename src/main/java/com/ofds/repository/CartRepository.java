package com.ofds.repository;

import com.ofds.entity.CartEntity;
import com.ofds.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing persistence operations (CRUD) for the CartEntity.
 */
@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {
    
    Optional<CartEntity> findByCustomer(CustomerEntity customer);
}