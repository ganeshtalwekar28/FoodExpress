package com.ofds.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ofds.entity.DeliveryAgentEntity;

/**
 * Spring Data JPA Repository for managing DeliveryAgentEntity data.
 */
@Repository
public interface DeliveryAgentRepository extends JpaRepository<DeliveryAgentEntity, Long>{

    /**
     * Counts the number of agents with a specific status (e.g., "BUSY").
     *
     * @param status The status string.
     * @return The count of agents with that status.
     */
    long countByStatus(String status);

    /**
     * Retrieves all agents and eagerly fetches the associated `ordersDelivered` collection
     * using a LEFT JOIN FETCH to prevent N+1 issues when accessing the orders list.
     *
     * @return A list of DeliveryAgentEntity objects with eagerly loaded orders.
     */
    @Query("SELECT DISTINCT a FROM DeliveryAgentEntity a LEFT JOIN FETCH a.ordersDelivered")
    List<DeliveryAgentEntity> findAllWithOrdersEagerly();

    /**
     * Finds agents based on their status using Spring Data JPA's derived query feature.
     *
     * @param status The status string (e.g., "AVAILABLE").
     * @return A list of agents matching the status.
     */
    List<DeliveryAgentEntity> findByStatus(String status);

    /**
     * Finds a single agent by ID, eagerly fetching all associated orders.
     * Used for retrieving detailed agent information.
     *
     * @param id The ID of the agent.
     * @return An Optional containing the DeliveryAgentEntity with orders loaded.
     */
    @Query("SELECT a FROM DeliveryAgentEntity a LEFT JOIN FETCH a.ordersDelivered WHERE a.id = :id")
    Optional<DeliveryAgentEntity> findByIdWithOrders(@Param("id") Long id);
}