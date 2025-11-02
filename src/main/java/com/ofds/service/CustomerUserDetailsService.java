package com.ofds.service;


import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.ofds.entity.CustomerEntity;
import com.ofds.repository.CustomerRepository;


/*
 * Keeps business logic (CustomerService) separate from authentication logic (CustomerUserDetailsService)

Ensures Spring Security can properly wire the UserDetailsService bean
 * */

@Service
public class CustomerUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerRepository custRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        CustomerEntity user = custRepo.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
            user.getEmail(), user.getPassword(), new ArrayList<>());
    }
}
