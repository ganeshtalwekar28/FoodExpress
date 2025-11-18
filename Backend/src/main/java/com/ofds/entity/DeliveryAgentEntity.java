package com.ofds.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the persistence model for a delivery agent, storing their credentials, status, 
 * performance metrics, and current/historical order assignments.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "delivery_agent")
public class DeliveryAgentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String agentCode;
    private String name;
    private String email;
    private String phone;
    private String password;
    private String status;
    private Integer totalDeliveries;
    private Double totalEarnings;
    private Double todaysEarning;
    
    @Column(name = "rating") 
    private Double rating;

    @OneToOne(mappedBy = "agent", fetch = FetchType.LAZY)
    private OrderEntity currentOrder;
    
    @OneToMany(mappedBy = "agent", fetch = FetchType.LAZY)
    private List<OrderEntity> ordersDelivered = new ArrayList<>();
}