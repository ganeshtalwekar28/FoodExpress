package com.ofds.controller;

import com.ofds.dto.RazorpayOrderResponse;
import com.ofds.service.PaymentService;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/auth/payment")
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @PostMapping("/createOrder")
    public ResponseEntity<?> createRazorpayOrder(@RequestParam Double amount, @RequestParam String currency) {
        try {
            RazorpayOrderResponse response = paymentService.createRazorpayOrder(amount, currency);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RazorpayException e) {
            logger.error("RazorpayException while creating order: {}", e.getMessage(), e);

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Razorpay Order Creation Failed");
            errorResponse.put("details", e.getMessage());
            
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); 
        }
    }
}