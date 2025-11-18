package com.ofds.entity;

import lombok.Data;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the core persistence model for a customer order, tracking all transaction details,
 * associated entities (customer, restaurant, agent), and payment information.
 */
@Entity
@Table(name = "orders")
@Data
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity user; 

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private RestaurantEntity restaurant;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "delivery_address")
    private String deliveryAddress;

    @Column(name = "total_amount")
    private Double totalAmount;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private DeliveryAgentEntity agent;
    
    @Column(name = "order_status")
    private String orderStatus; 
    
    @Column(name = "payment_status")
    private String paymentStatus; 
    
    @Column(name = "payment_method")
    private String paymentMethod = "Razorpay";
    
    @Column(name = "razorpay_order_id")
    private String razorpayOrderId; 
    
    @Column(name = "razorpay_payment_id")
    private String razorpayPaymentId;
    
    @Column(name = "razorpay_signature")
    private String razorpaySignature;

    @Column(name = "estimated_delivery")
    private LocalDateTime estimatedDelivery;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> items;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY) // LAZY is fine with FETCH JOIN
    private List<OrderItemEntity> itemList = new ArrayList<>();
}