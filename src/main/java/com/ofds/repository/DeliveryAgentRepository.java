package com.ofds.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ofds.entity.DeliveryAgentEntity;

/**
 * Repository interface for managing persistence operations (CRUD) for the DeliveryAgentEntity, 
 * including custom queries for status counts and eager loading of associated orders.
 */
@Repository
public interface DeliveryAgentRepository extends JpaRepository<DeliveryAgentEntity, Long>{

    /**
     * Counts the total number of delivery agents currently matching the specified status.
     */
    long countByStatus(String status);

    /**
     * Retrieves all agents, eagerly loading their list of delivered orders to avoid N+1 issues.
     */
    @Query("SELECT DISTINCT a FROM DeliveryAgentEntity a LEFT JOIN FETCH a.ordersDelivered")
    List<DeliveryAgentEntity> findAllWithOrdersEagerly();

    /**
     * Finds and returns a list of delivery agents whose current status matches the given status.
     */
    List<DeliveryAgentEntity> findByStatus(String status);

    /**
     * Finds a single agent by ID, eagerly fetching all associated orders for detailed viewing.
     */
    @Query("SELECT a FROM DeliveryAgentEntity a LEFT JOIN FETCH a.ordersDelivered WHERE a.id = :id")
    Optional<DeliveryAgentEntity> findByIdWithOrders(@Param("id") Long id);
}