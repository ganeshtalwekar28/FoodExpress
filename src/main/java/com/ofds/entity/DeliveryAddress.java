package com.ofds.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class DeliveryAddress {
    
    // We break down the name for proper database column mapping
    private String firstName;
    private String lastName;
    
    private String email;
    private String phoneNumber;
    
    // Address components
    private String address;
    private String state;
    private String city;
    private String zip;
}
