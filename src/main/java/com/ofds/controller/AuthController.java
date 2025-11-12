package com.ofds.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import com.ofds.config.JwtUtils;
import com.ofds.dto.AuthRequest;
import com.ofds.entity.CustomerEntity;
import com.ofds.repository.CustomerRepository;
import com.ofds.service.CustomerService;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

	private final CustomerService customerUserDetailsService;
    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final CustomerRepository custRepo;

    // Constructor injection for all dependencies
    public AuthController(
        CustomerService customUserDetailsService,
        AuthenticationManager authManager,
        JwtUtils jwtUtils,
        PasswordEncoder passwordEncoder,
        CustomerRepository custRepo
    ) {
        this.customerUserDetailsService = customUserDetailsService;
        this.authManager = authManager;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.custRepo = custRepo;
    }

    /**
     * Handles user login by authenticating credentials and generating a JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthRequest request) {
        System.out.println("Inside login()...");

        Authentication authentication = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        String token = jwtUtils.generateToken(request.getEmail());
        System.out.println("Token : " + token);

        CustomerEntity user = custRepo.findByEmail(request.getEmail()).orElse(null);

        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", user);

        return ResponseEntity.ok(response);
    }

    /**
     * Registers a new customer by checking for existing email and encoding the password before saving.
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest request) {
        if (custRepo.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        CustomerEntity user = new CustomerEntity();
        user.setEmail(request.getEmail());
        user.setPassword(encodedPassword);
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setTermsAccepted(true);
        custRepo.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    /**
     * Updates the details (name, phone, email, password) of the currently authenticated customer.
     */
    @PutMapping("/update")
    public ResponseEntity<String> updateCustomer(@RequestHeader("Authorization") String token, @RequestBody AuthRequest request) {
        // Extract current email from JWT
        String currentEmail = jwtUtils.extractUsername(token.replace("Bearer ", ""));

        CustomerEntity existingUser = custRepo.findByEmail(currentEmail).orElse(null);
        if (existingUser == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        // ✅ Check if new email is provided and different
        if (request.getEmail() != null && !request.getEmail().isBlank() && !request.getEmail().equals(currentEmail)) {
            // ✅ Check if new email already exists
            if (custRepo.findByEmail(request.getEmail()).isPresent()) {
                return ResponseEntity.status(409).body("Email already in use");
            }
            existingUser.setEmail(request.getEmail());
        }

        // ✅ Update other fields
        if (request.getName() != null && !request.getName().isBlank()) {
            existingUser.setName(request.getName());
        }
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            existingUser.setPhone(request.getPhone());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            existingUser.setPassword(encodedPassword);
        }

        custRepo.save(existingUser);

        // ✅ Inform user to re-login if email was changed
        if (!currentEmail.equals(existingUser.getEmail())) {
            return ResponseEntity.ok("Email updated successfully. Please log in again with your new email.");
        }
        return ResponseEntity.ok("User updated successfully");
    }
}