package com.ofds.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_item") // Renamed table to match standard practice (singular/plural)
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // FIX 1: Changed to Long (BIGINT) for consistency with OrderEntity

    private Integer quantity;

    @Column(name = "price") 
    private Double price; // Renamed from unitPrice

    // FIX 3 (CRITICAL): Re-establishing the proper ManyToOne relationship to MenuItemEntity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItemEntity menuItem; // This is what OrdersService expects to set

    // FIX 4: Corrected variable name to 'order' to match OrderEntity's mappedBy field
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order; 
    
    // FIX 5: Added item name (snapshot) to match the service logic
    // This is useful if the menu item name is later changed in the MenuItemEntity.
    private String name; 
    private String imageUrl;
}