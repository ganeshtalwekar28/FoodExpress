package com.ofds.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a reusable and embeddable entity component that stores the full delivery address and contact details 
 * for an order or customer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class DeliveryAddress {
    
    private String firstName;
    private String lastName;
    
    private String email;
    private String phoneNumber;
    
    private String address;
    private String state;
    private String city;
    private String zip;
}