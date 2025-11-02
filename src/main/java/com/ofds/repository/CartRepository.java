package com.ofds.repository;

import com.ofds.entity.CartEntity;
import com.ofds.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<CartEntity, Integer> {
    Optional<CartEntity> findByCustomer(CustomerEntity customer);
}
