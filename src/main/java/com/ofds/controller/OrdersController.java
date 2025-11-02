package com.ofds.controller;

import java.util.List; // Needed for the list of past orders

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ofds.dto.OrderRequest;
import com.ofds.dto.OrderResponse;
import com.ofds.exception.DataNotFoundException;
import com.ofds.service.OrdersService;

@RestController
@RequestMapping("/api/auth/orders")
@CrossOrigin(origins = "http://localhost:4200") 
public class OrdersController {

    @Autowired
    private OrdersService ordersService; 

    // 1. FINAL ORDER PLACEMENT (Required by finalizeOrder in frontend)
    // Endpoint: POST /api/auth/orders/place
    @PostMapping("/place")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest) {
        try {
            OrderResponse response = ordersService.placeOrder(orderRequest);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
            
        } catch (DataNotFoundException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
            
        } catch (IllegalStateException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
            
        } catch (Exception e) {
            System.err.println("Unexpected error during order placement: " + e.getMessage());
            return new ResponseEntity("Order placement failed due to a server error.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // 2. FETCH USER HISTORY (Required by getOrdersByUser in frontend)
    // Endpoint: GET /api/auth/orders/user/{userID}
    @GetMapping("/user/{userID}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUser(@PathVariable Integer userID) {
        try {
            // NOTE: You need to implement getOrdersHistory in OrdersService
            List<OrderResponse> orders = ordersService.getOrdersHistory(userID);
            
            if (orders.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
            }
            return new ResponseEntity<>(orders, HttpStatus.OK);
            
        } catch (DataNotFoundException e) {
            // Catch if the user ID doesn't exist
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("Error fetching user orders: " + e.getMessage());
            return new ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}