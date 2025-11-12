package com.ofds.repository;

import com.ofds.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing persistence operations (CRUD) for the OrderEntity, 
 * including complex queries for fetching detailed order information, updating status, and calculating metrics.
 */
@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    
    /**
     * Finds a list of orders associated with a specific user ID, sorted by the most recent order date first.
     */
    List<OrderEntity> findByUserIdOrderByOrderDateDesc(Long userId);
    

    /**
     * Retrieves all orders with full details (Customer, Restaurant, Agent, Items)
     * using FETCH JOIN to prevent LazyInitializationException when mapping to DTOs.
     *
     * @return A list of all OrdersEntity objects with eagerly loaded associations.
     */
    @Query("SELECT DISTINCT o FROM OrderEntity o " +
            "JOIN FETCH o.user c " +
            "JOIN FETCH o.restaurant r " +
            "LEFT JOIN FETCH o.agent a " +
            "LEFT JOIN FETCH o.items oi")
    List<OrderEntity> findAllOrdersWithDetails();


    /**
     * Counts the number of orders that match a specific orderStatus.
     */
    long countByOrderStatus(String orderStatus);

    /**
     * Calculates the sum of the totalAmount for all orders that have been marked as 'DELIVERED'.
     */
    @Query("SELECT SUM(o.totalAmount) FROM OrderEntity o WHERE o.orderStatus = 'DELIVERED'")
    Double sumTotalAmountByStatusDelivered();

    /**
     * Finds an active order (status 'OUT FOR DELIVERY') for a given agent ID.
     */
    @Query("SELECT o FROM OrderEntity o WHERE o.agent.id = :agentId AND " +
            "(UPPER(o.orderStatus) = 'OUT FOR DELIVERY' OR UPPER(o.orderStatus) = 'OUT_FOR_DELIVERY')")
    Optional<OrderEntity> findActiveOrderByAgentId(@Param("agentId") Long agentId);

    /**
     * Retrieves a single order by ID, eagerly fetching its associated line items for the detailed order view.
     */
    @Query("SELECT DISTINCT o FROM OrderEntity o LEFT JOIN FETCH o.items WHERE o.id = :id")
    Optional<OrderEntity> findByIdWithItems(@Param("id") Long id);

    /**
     * Updates the orderStatus of an order directly by ID using a modifying query.
     */
    @Modifying
    @Query("UPDATE OrderEntity o SET o.orderStatus = :orderStatus WHERE o.id = :id")
    int updateOrderStatusById(@Param("id") Long id, @Param("orderStatus") String orderStatus);
}