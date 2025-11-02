package com.ofds.controller;

import com.ofds.dto.CartDTO;
import com.ofds.exception.DataNotFoundException;
import com.ofds.service.CartService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/carts")
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class CartController {

    @Autowired
    private CartService cartService;

    // GET /api/carts/customer/{customerId}
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<CartDTO> getCartByCustomer(@PathVariable Integer customerId) throws DataNotFoundException {
        CartDTO cart = cartService.getCartByCustomerId(customerId);
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    // POST /api/carts/customer/{customerId}/restaurant/{restaurantId}/items/{menuItemId}?quantity=2
    @PostMapping("/customer/{customerId}/restaurant/{restaurantId}/items/{menuItemId}")
    public ResponseEntity<CartDTO> addItemToCart(
            @PathVariable Integer customerId,
            @PathVariable Integer restaurantId,
            @PathVariable Integer menuItemId,
            @RequestParam int quantity) throws DataNotFoundException {
        CartDTO updatedCart = cartService.addItem(customerId, restaurantId, menuItemId, quantity);
        return new ResponseEntity<>(updatedCart, HttpStatus.CREATED);
    }

    // PUT /api/carts/customer/{customerId}/items/{cartItemId}?quantity=3
    @PutMapping("/customer/{customerId}/items/{cartItemId}")
    public ResponseEntity<CartDTO> updateItemQuantity(
            @PathVariable Integer customerId,
            @PathVariable Integer cartItemId,
            @RequestParam int quantity) throws DataNotFoundException {

        CartDTO updatedCart = cartService.updateQuantity(customerId, cartItemId, quantity);
        if (updatedCart == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // cart deleted
        }
        return new ResponseEntity<>(updatedCart, HttpStatus.OK);
    }

    // DELETE /api/carts/customer/{customerId}/items/{cartItemId}
    @DeleteMapping("/customer/{customerId}/items/{cartItemId}")
    public ResponseEntity<CartDTO> removeItemFromCart(
            @PathVariable Integer customerId,
            @PathVariable Integer cartItemId) throws DataNotFoundException {
        CartDTO updatedCart = cartService.updateQuantity(customerId, cartItemId, Integer.MIN_VALUE); // force removal
        if (updatedCart == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(updatedCart, HttpStatus.OK);
    }

    // DELETE /api/carts/customer/{customerId}
    @DeleteMapping("/customer/{customerId}")
    public ResponseEntity<Void> clearCart(@PathVariable Integer customerId) throws DataNotFoundException {
        cartService.clearCart(customerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
