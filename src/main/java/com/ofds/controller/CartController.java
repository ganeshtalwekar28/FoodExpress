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

    /**
     * Retrieves the current shopping cart details for a specific customer.
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<CartDTO> getCartByCustomer(@PathVariable Long customerId) throws DataNotFoundException {
        CartDTO cart = cartService.getCartByCustomerId(customerId);
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    /**
     * Adds a new menu item to the customer's cart or increments its quantity.
     */
    @PostMapping("/customer/{customerId}/restaurant/{restaurantId}/items/{menuItemId}")
    public ResponseEntity<CartDTO> addItemToCart(
            @PathVariable Long customerId,
            @PathVariable Long restaurantId,
            @PathVariable Long menuItemId,
            @RequestParam int quantity) throws DataNotFoundException {
        CartDTO updatedCart = cartService.addItem(customerId, restaurantId, menuItemId, quantity);
        return new ResponseEntity<>(updatedCart, HttpStatus.CREATED);
    }

    /**
     * Updates the quantity of an existing item in the customer's cart.
     */
    @PutMapping("/customer/{customerId}/items/{cartItemId}")
    public ResponseEntity<CartDTO> updateItemQuantity(
            @PathVariable Long customerId,
            @PathVariable Long cartItemId,
            @RequestParam int quantity) throws DataNotFoundException {

        CartDTO updatedCart = cartService.updateQuantity(customerId, cartItemId, quantity);
        if (updatedCart == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // cart deleted
        }
        return new ResponseEntity<>(updatedCart, HttpStatus.OK);
    }

    /**
     * Removes a specific item completely from the customer's cart.
     */
    @DeleteMapping("/customer/{customerId}/items/{cartItemId}")
    public ResponseEntity<CartDTO> removeItemFromCart(
            @PathVariable Long customerId,
            @PathVariable Long cartItemId) throws DataNotFoundException {
        CartDTO updatedCart = cartService.updateQuantity(customerId, cartItemId, Integer.MIN_VALUE); // force removal
        if (updatedCart == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(updatedCart, HttpStatus.OK);
    }

    /**
     * Clears all items from the customer's shopping cart.
     */
    @DeleteMapping("/customer/{customerId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long customerId) throws DataNotFoundException {
        cartService.clearCart(customerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}