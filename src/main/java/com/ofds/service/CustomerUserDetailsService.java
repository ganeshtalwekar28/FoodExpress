package com.ofds.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.ofds.entity.CustomerEntity;
import com.ofds.repository.CustomerRepository;

/**
 * Service class that implements Spring Security's UserDetailsService, responsible for fetching 
 * customer details (username, password, roles) for authentication purposes.
 */
@Service
public class CustomerUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerRepository custRepo;

    /**
     * Locates a customer's record by email address and returns a Spring Security UserDetails object.
     * Throws an exception if the customer is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        CustomerEntity user = custRepo.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Creates a Spring Security User object with the user's email, password, and an empty list of authorities/roles
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(), user.getPassword(), new ArrayList<>());
    }
}