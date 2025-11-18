package com.ofds.repository;

import com.ofds.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing persistence operations (CRUD) for the CustomerEntity.
 */
@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    

    Optional<CustomerEntity> findByEmail(String email);

    Optional<CustomerEntity> findByPhone(String phone);
}