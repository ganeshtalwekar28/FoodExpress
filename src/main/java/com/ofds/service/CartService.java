////package com.ofds.service;
////
////import com.ofds.dto.CartDTO;
////import com.ofds.entity.*;
////import com.ofds.exception.DataNotFoundException;
////import com.ofds.config.CartMapper;
////import com.ofds.repository.*;
////
////import lombok.RequiredArgsConstructor;
////
////import org.springframework.stereotype.Service;
////
////import java.util.*;
////
////@Service
////@RequiredArgsConstructor
////public class CartService {
////
////    private final CartRepository cartRepository;
////    private final CustomerRepository customerRepository;
////    private final RestaurantRepository restaurantRepository;
////    private final MenuItemRepository menuItemRepository;
////    private final CartItemRepository cartItemRepository;
////    private final CartMapper cartMapper;
////
////    public CartDTO getCartByCustomerId(Integer customerId) throws DataNotFoundException {
////        CustomerEntity customer = customerRepository.findById(customerId)
////                .orElseThrow(() -> new DataNotFoundException("Customer not found"));
////
////        CartEntity cart = cartRepository.findByCustomer(customer)
////                .orElseThrow(() -> new DataNotFoundException("Cart not found"));
////
////        return cartMapper.toDTO(cart);}
////
////    public CartDTO addItem(Integer customerId, Integer restaurantId, Integer menuItemId, int quantity) throws DataNotFoundException {
////        CustomerEntity customer = customerRepository.findById(customerId)
////                .orElseThrow(() -> new DataNotFoundException("Customer not found"));
////
////        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
////                .orElseThrow(() -> new DataNotFoundException("Restaurant not found"));
////
////        MenuItemEntity menuItem = menuItemRepository.findById(menuItemId)
////                .orElseThrow(() -> new DataNotFoundException("Menu item not found"));
////
////        CartEntity cart = cartRepository.findByCustomer(customer).orElse(null);
////
////        if (cart == null) {
////            cart = new CartEntity();
////            cart.setCustomer(customer);
////            cart.setRestaurant(restaurant); 
////            cart.setItems(new ArrayList<>());
////        } else if (!cart.getRestaurant().getId().equals(restaurantId)) {
////            throw new IllegalStateException("Cart already contains items from another restaurant");
////        }
////
////        CartItemEntity cartItem = new CartItemEntity();
////        cartItem.setCart(cart);
////        cartItem.setMenuItem(menuItem);
////        cartItem.setQuantity(quantity);
////        cart.getItems().add(cartItem);
////
////        updateCartTotals(cart);
////        cartRepository.save(cart);
////        cartItemRepository.save(cartItem);
////
////        return cartMapper.toDTO(cart);
////    }
////
////    public CartDTO updateQuantity(Integer customerId, Integer cartItemId, int quantity) throws DataNotFoundException {
////        CustomerEntity customer = customerRepository.findById(customerId)
////                .orElseThrow(() -> new DataNotFoundException("Customer not found"));
////
////        CartEntity cart = cartRepository.findByCustomer(customer)
////                .orElseThrow(() -> new DataNotFoundException("Cart not found"));
////
////        CartItemEntity item = cartItemRepository.findById(cartItemId)
////                .orElseThrow(() -> new DataNotFoundException("Cart item not found"));
////
////        if (!cart.getItems().contains(item)) {
////            throw new DataNotFoundException("Item does not belong to customer's cart");
////        }
////
////        int newQuantity = item.getQuantity() + quantity;
////
////        if (newQuantity <= 0) {
////            cart.getItems().remove(item);
////            cartItemRepository.delete(item);
////        } else {
////            item.setQuantity(newQuantity);
////            cartItemRepository.save(item);
////        }
////
////        updateCartTotals(cart);
////        cartRepository.save(cart);
////
////        return cartMapper.toDTO(cart);
////    }
////
////    public CartDTO removeItem(Integer customerId, Integer cartItemId) throws DataNotFoundException {
////        return updateQuantity(customerId, cartItemId, 0);
////    }
////
////    public void clearCart(Integer customerId) throws DataNotFoundException {
////        CustomerEntity customer = customerRepository.findById(customerId)
////                .orElseThrow(() -> new DataNotFoundException("Customer not found"));
////
////        CartEntity cart = cartRepository.findByCustomer(customer).orElse(null);
////
////        if (cart != null) {
////            cartItemRepository.deleteAll(cart.getItems());
////            cartRepository.delete(cart);
////        }
////    }
////
////    private void updateCartTotals(CartEntity cart) {
////        int itemCount = 0;
////        double totalAmount = 0.0;
////
////        for (CartItemEntity item : cart.getItems()) {
////            itemCount += 1;
////            totalAmount += item.getMenuItem().getPrice() * item.getQuantity();
////        }
////
////        cart.setItemCount(itemCount);
////        cart.setTotalAmount(totalAmount);
////    }
////}
//
//
//package com.ofds.service;
//
//import com.ofds.dto.CartDTO;
//import com.ofds.entity.*;
//import com.ofds.exception.DataNotFoundException;
//import com.ofds.config.CartMapper;
//import com.ofds.repository.*;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//
//@Service
//@RequiredArgsConstructor
//public class CartService {
//
//    private final CartRepository cartRepository;
//    private final CustomerRepository customerRepository;
//    private final RestaurantRepository restaurantRepository;
//    private final MenuItemRepository menuItemRepository;
//    private final CartItemRepository cartItemRepository;
//    private final CartMapper cartMapper;
//
//    public CartDTO getCartByCustomerId(Integer customerId) throws DataNotFoundException {
//        CustomerEntity customer = customerRepository.findById(customerId)
//                .orElseThrow(() -> new DataNotFoundException("Customer not found"));
//
//        CartEntity cart = cartRepository.findByCustomer(customer)
//                .orElseThrow(() -> new DataNotFoundException("Cart not found"));
//
//        return cartMapper.toDTO(cart);
//    }
//
//    public CartDTO addItem(Integer customerId, Integer restaurantId, Integer menuItemId, int quantity) throws DataNotFoundException {
//        CustomerEntity customer = customerRepository.findById(customerId)
//                .orElseThrow(() -> new DataNotFoundException("Customer not found"));
//
//        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
//                .orElseThrow(() -> new DataNotFoundException("Restaurant not found"));
//
//        MenuItemEntity menuItem = menuItemRepository.findById(menuItemId)
//                .orElseThrow(() -> new DataNotFoundException("Menu item not found"));
//
//        CartEntity cart = cartRepository.findByCustomer(customer).orElse(null);
//
//        if (cart == null) {
//            cart = new CartEntity();
//            cart.setCustomer(customer);
//            cart.setRestaurant(restaurant);
//            cart.setItems(new ArrayList<>());
//        } else if (!cart.getRestaurant().getId().equals(restaurantId)) {
//            throw new IllegalStateException("Cart already contains items from another restaurant");
//        }
//
//        Optional<CartItemEntity> existingItem = cart.getItems().stream()
//                .filter(i -> i.getMenuItem().getId().equals(menuItemId))
//                .findFirst();
//
//        if (existingItem.isPresent()) {
//            CartItemEntity item = existingItem.get();
//            item.setQuantity(item.getQuantity() + quantity);
//            cartItemRepository.save(item);
//        } else {
//            CartItemEntity cartItem = new CartItemEntity();
//            cartItem.setCart(cart);
//            cartItem.setMenuItem(menuItem);
//            cartItem.setQuantity(quantity);
//            cart.getItems().add(cartItem);
//            cartItemRepository.save(cartItem);
//        }
//
//        updateCartTotals(cart);
//        cartRepository.save(cart);
//
//        return cartMapper.toDTO(cart);
//    }
//
//    public CartDTO updateQuantity(Integer customerId, Integer cartItemId, int quantity) throws DataNotFoundException {
//        CustomerEntity customer = customerRepository.findById(customerId)
//                .orElseThrow(() -> new DataNotFoundException("Customer not found"));
//
//        CartEntity cart = cartRepository.findByCustomer(customer)
//                .orElseThrow(() -> new DataNotFoundException("Cart not found"));
//
//        CartItemEntity item = cartItemRepository.findById(cartItemId)
//                .orElseThrow(() -> new DataNotFoundException("Cart item not found"));
//
//        if (!cart.getItems().contains(item)) {
//            throw new DataNotFoundException("Item does not belong to customer's cart");
//        }
//
//        int newQuantity = item.getQuantity() + quantity;
//
//        if (newQuantity <= 0) {
//            cart.getItems().removeIf(i -> i.getId().equals(cartItemId));
//            cartItemRepository.delete(item);
//        } else {
//            item.setQuantity(newQuantity);
//            cartItemRepository.save(item);
//        }
//
//        updateCartTotals(cart);
//        cartRepository.save(cart);
//
//        return cartMapper.toDTO(cart);
//    }
//
//    public CartDTO removeItem(Integer customerId, Integer cartItemId) throws DataNotFoundException {
//        return updateQuantity(customerId, cartItemId, -1 * Integer.MAX_VALUE); // force removal
//    }
//
//    public void clearCart(Integer customerId) throws DataNotFoundException {
//        CustomerEntity customer = customerRepository.findById(customerId)
//                .orElseThrow(() -> new DataNotFoundException("Customer not found"));
//
//        CartEntity cart = cartRepository.findByCustomer(customer).orElse(null);
//
//        if (cart != null) {
//            cartItemRepository.deleteAll(cart.getItems());
//            cartRepository.delete(cart);
//        }
//    }
//
//    private void updateCartTotals(CartEntity cart) {
//        int itemCount = 0;
//        double totalAmount = 0.0;
//
//        for (CartItemEntity item : cart.getItems()) {
//            itemCount += item.getQuantity();
//            totalAmount += item.getMenuItem().getPrice() * item.getQuantity();
//        }
//
//        cart.setItemCount(itemCount);
//        cart.setTotalAmount(totalAmount);
//    }
//}
package com.ofds.service;

import com.ofds.dto.CartDTO;
import com.ofds.entity.*;
import com.ofds.exception.DataNotFoundException;
import com.ofds.config.CartMapper;
import com.ofds.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
    


    public CartDTO getCartByCustomerId(Integer customerId) throws DataNotFoundException {
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new DataNotFoundException("Customer not found"));

        CartEntity cart = cartRepository.findByCustomer(customer)
                .orElseThrow(() -> new DataNotFoundException("Cart not found"));

        return cartMapper.toDTO(cart);
    }

    public CartDTO addItem(Integer customerId, Integer restaurantId, Integer menuItemId, int quantity) throws DataNotFoundException {
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new DataNotFoundException("Customer not found"));

        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new DataNotFoundException("Restaurant not found"));

        MenuItemEntity menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new DataNotFoundException("Menu item not found"));

        CartEntity cart = cartRepository.findByCustomer(customer).orElse(null);

        if (cart != null && cart.getItemCount() == 0) {
            cartItemRepository.deleteAll(cart.getItems());
            cartRepository.delete(cart);
            cart = null;
        }

        if (cart == null) {
            cart = new CartEntity();
            cart.setCustomer(customer);
            cart.setRestaurant(restaurant);
            cart.setItems(new ArrayList<>());
            cart = cartRepository.save(cart);
        } else if (cart.getRestaurant() != null && !cart.getRestaurant().getId().equals(restaurantId)) {
            throw new IllegalStateException("Cart already contains items from another restaurant");
        }

        Optional<CartItemEntity> existingItem = cart.getItems().stream()
                .filter(i -> i.getMenuItem().getId().equals(menuItemId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItemEntity item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
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

    public CartDTO updateQuantity(Integer customerId, Integer cartItemId, int quantityDelta) throws DataNotFoundException {
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new DataNotFoundException("Customer not found"));

        CartEntity cart = cartRepository.findByCustomer(customer)
                .orElseThrow(() -> new DataNotFoundException("Cart not found"));

        CartItemEntity item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new DataNotFoundException("Cart item not found"));

        if (cart.getItems().stream().noneMatch(i -> i.getId().equals(cartItemId))) {
            throw new DataNotFoundException("Item does not belong to customer's cart");
        }

        int newQuantity = item.getQuantity() + quantityDelta;

        if (newQuantity <= 0) {
            cart.getItems().removeIf(i -> i.getId().equals(cartItemId));
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
        }

        updateCartTotals(cart);

        if (cart.getItemCount() == 0) {
            cartItemRepository.deleteAll(cart.getItems());
            cartRepository.delete(cart);
            return null;
        }

        cartRepository.save(cart);
        return cartMapper.toDTO(cart);
    }

    public CartDTO removeItem(Integer customerId, Integer cartItemId) throws DataNotFoundException {
        return updateQuantity(customerId, cartItemId, -Integer.MAX_VALUE);
    }

    public void clearCart(Integer customerId) throws DataNotFoundException {
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new DataNotFoundException("Customer not found"));

        CartEntity cart = cartRepository.findByCustomer(customer).orElse(null);

        if (cart != null) {
            cartItemRepository.deleteAll(cart.getItems());
            cartRepository.delete(cart);
        }
    }

    private void updateCartTotals(CartEntity cart) {
        int itemCount = 0;
        double subtotal = 0.0;

        // Loop through each item in the cart to calculate the subtotal.
        for (CartItemEntity item : cart.getItems()) {
            itemCount += item.getQuantity();
            subtotal += item.getMenuItem().getPrice() * item.getQuantity();
        }

        cart.setItemCount(itemCount);
        // Set the totalAmount to the calculated subtotal.
        cart.setTotalAmount(subtotal);
    }
}
