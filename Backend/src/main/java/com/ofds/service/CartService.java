package com.ofds.service;

import com.ofds.dto.CartDTO;
import com.ofds.entity.*;
import com.ofds.exception.DataNotFoundException;
import com.ofds.mapper.CartMapper;
import com.ofds.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service class for managing all business logic related to the customer's shopping cart, 
 * including retrieval, adding/removing items, and updating quantities.
 */
@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartMapper cartMapper;
    
    /**
     * Retrieves the active shopping cart for a given customer ID and maps it to a DTO.
     */
    public CartDTO getCartByCustomerId(Long i) throws DataNotFoundException {
        CustomerEntity customer = customerRepository.findById(i)
                .orElseThrow(() -> new DataNotFoundException("Customer not found"));

        CartEntity cart = cartRepository.findByCustomer(customer)
                .orElseThrow(() -> new DataNotFoundException("Cart not found"));

        return cartMapper.toDTO(cart);
    }

    /**
     * Adds a specified quantity of a menu item to the customer's cart, handling creation 
     * of a new cart or updating an existing item's quantity.
     */
    public CartDTO addItem(Long customerId, Long restaurantId, Long menuItemId, int quantity) throws DataNotFoundException {
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new DataNotFoundException("Customer not found"));

        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new DataNotFoundException("Restaurant not found"));

        MenuItemEntity menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new DataNotFoundException("Menu item not found"));

        CartEntity cart = cartRepository.findByCustomer(customer).orElse(null);

        // Deletes empty cart if found
        if (cart != null && cart.getItemCount() == 0) {
            cartItemRepository.deleteAll(cart.getItems());
            cartRepository.delete(cart);
            cart = null;
        }

        // Creates new cart if none exists
        if (cart == null) {
            cart = new CartEntity();
            cart.setCustomer(customer);
            cart.setRestaurant(restaurant);
            cart.setItems(new ArrayList<>());
            cart = cartRepository.save(cart);
        } else if (cart.getRestaurant() != null && !cart.getRestaurant().getId().equals(restaurantId)) {
            // Enforces single-restaurant per cart rule
            throw new IllegalStateException("Cart already contains items from another restaurant");
        }

        // Checks if item already exists in cart
        Optional<CartItemEntity> existingItem = cart.getItems().stream()
                .filter(i -> i.getMenuItem().getId().equals(menuItemId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItemEntity item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            // Adds new item
            CartItemEntity cartItem = new CartItemEntity();
            cartItem.setCart(cart);
            cartItem.setMenuItem(menuItem);
            cartItem.setQuantity(quantity);
            cart.getItems().add(cartItem);
            cartItemRepository.save(cartItem);
        }

        updateCartTotals(cart);
        cartRepository.save(cart);

        return cartMapper.toDTO(cart);
    }

    /**
     * Updates the quantity of a specific item in the cart by the given delta. 
     * Handles removal if new quantity is zero or less.
     */
    public CartDTO updateQuantity(Long customerId, Long cartItemId, int quantityDelta) throws DataNotFoundException {
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new DataNotFoundException("Customer not found"));

        CartEntity cart = cartRepository.findByCustomer(customer)
                .orElseThrow(() -> new DataNotFoundException("Cart not found"));

        CartItemEntity item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new DataNotFoundException("Cart item not found"));

        // Verifies item belongs to this cart
        if (cart.getItems().stream().noneMatch(i -> i.getId().equals(cartItemId))) {
            throw new DataNotFoundException("Item does not belong to customer's cart");
        }

        int newQuantity = item.getQuantity() + quantityDelta;

        if (newQuantity <= 0) {
            // Remove item from cart if quantity is zero or less
            cart.getItems().removeIf(i -> i.getId().equals(cartItemId));
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
        }

        updateCartTotals(cart);

        // Delete cart entity if it becomes empty
        if (cart.getItemCount() == 0) {
            cartItemRepository.deleteAll(cart.getItems());
            cartRepository.delete(cart);
            return null;
        }

        cartRepository.save(cart);
        return cartMapper.toDTO(cart);
    }

    /**
     * Removes a specific cart item by delegating to updateQuantity with a large negative delta.
     */
    public CartDTO removeItem(Long customerId, Long cartItemId) throws DataNotFoundException {
        return updateQuantity(customerId, cartItemId, -Integer.MAX_VALUE);
    }

    /**
     * Clears the customer's cart by deleting all associated cart items and the cart entity itself.
     */
    public void clearCart(Long customerId) throws DataNotFoundException {
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new DataNotFoundException("Customer not found"));

        CartEntity cart = cartRepository.findByCustomer(customer).orElse(null);

        if (cart != null) {
            cartItemRepository.deleteAll(cart.getItems());
            cartRepository.delete(cart);
        }
    }

    /**
     * Recalculates the total item count and total monetary amount for the cart entity.
     */
    private void updateCartTotals(CartEntity cart) {
        int itemCount = 0;
        double subtotal = 0.0;

        for (CartItemEntity item : cart.getItems()) {
            itemCount += item.getQuantity();
            // Calculation uses price from the associated MenuItemEntity
            subtotal += item.getMenuItem().getPrice() * item.getQuantity();
        }

        cart.setItemCount(itemCount);
        cart.setTotalAmount(subtotal);
    }
}