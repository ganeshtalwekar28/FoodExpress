package com.ofds.service;

import com.ofds.dto.CartDTO;
import com.ofds.dto.CartItemDTO; // Needed for iterating through cart items
import com.ofds.dto.OrderRequest;
import com.ofds.dto.OrderResponse; 
import com.ofds.entity.CustomerEntity;
import com.ofds.entity.MenuItemEntity;
import com.ofds.entity.OrderEntity;
import com.ofds.entity.OrderItemEntity;
import com.ofds.entity.RestaurantEntity;
import com.ofds.exception.DataNotFoundException;
import com.ofds.mapper.OrderMapper; // Corrected package to .mapper

import com.ofds.repository.CustomerRepository;
import com.ofds.repository.MenuItemRepository;
import com.ofds.repository.OrderRepository;
import com.ofds.repository.OrdersItemsRepository;
import com.ofds.repository.RestaurantRepository;

import jakarta.transaction.Transactional; // Annotation for transaction management

import java.time.LocalDateTime;
import java.util.ArrayList; // Needed for initializing List<OrderItemEntity>
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger; // Needed for logging
import org.slf4j.LoggerFactory; // Needed for logging

// Renaming the class to OrderService is common, but keeping OrdersService is fine.
/**
 * This service contains the business logic for handling orders.
 * It is responsible for placing new orders and retrieving order history.
 */
@Service 
public class OrdersService { 

    private static final Logger log = LoggerFactory.getLogger(OrdersService.class); 

    @Autowired
    OrderRepository orderRepository;
    
    @Autowired
    OrdersItemsRepository ordersItemsRepository;
    
    @Autowired
    CartService cartService;
    
    @Autowired
    RestaurantRepository restaurantRepository;
    
    @Autowired
    CustomerRepository customerRepository;
    
    @Autowired
    private MenuItemRepository menuItemRepository; // Injected directly

    @Autowired 
    OrderMapper orderMapper; 
    
    /**
     * Places a new order based on the items in the customer's cart.
     * This method is transactional, meaning if any step fails, all database changes will be rolled back.
     * @param request The OrderRequest DTO containing customer and payment info.
     * @return An OrderResponse DTO with details of the newly created order.
     * @throws DataNotFoundException if the customer or restaurant is not found.
     */
    @Transactional
    public OrderResponse placeOrder(OrderRequest request) throws DataNotFoundException {
        log.info("OrderRequest received: {}", request);
        
        // 1. Fetch the customer's cart to get the items for the order.
        CartDTO cartDTO = cartService.getCartByCustomerId(request.getCustomerId());
        
        // Check if the cart is empty. If so, we cannot place an order.
        if (cartDTO == null || cartDTO.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot place an order with an empty cart or non-existent cart.");
        }

        // 2. Create a new OrderEntity. This is the object that will be saved to the 'orders' table.
        OrderEntity order = new OrderEntity();
        
        // 3. Find the Customer and Restaurant entities from the database using their IDs.
        // We use orElseThrow to handle the case where the customer or restaurant is not found.
        CustomerEntity customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new DataNotFoundException("Customer not found"));

        RestaurantEntity restaurant = restaurantRepository.findById(cartDTO.getRestaurantId())
                .orElseThrow(() -> new DataNotFoundException("Restaurant not found"));
        
        // 4. Map the details from the request and cart to the OrderEntity.
        order.setUser(customer);
        order.setUserId(customer.getId()); // Manually set the second user/customer ID field
        order.setRestaurant(restaurant);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus("Placed");
        order.setPaymentStatus("Paid"); // Assuming payment is always successful in this simplified flow.
        order.setPaymentMethod("Razorpay"); // Changed to reflect real integration
        order.setTotalAmount(request.getTotalAmount());
        order.setDeliveryAddress(request.getDeliveryAddress());
        
        log.info("Setting RazorpayOrderId: {}, PaymentId: {}, Signature: {}", 
                 request.getRazorpayOrderId(), request.getRazorpayPaymentId(), request.getRazorpaySignature());

        order.setRazorpayOrderId(request.getRazorpayOrderId());
        order.setRazorpayPaymentId(request.getRazorpayPaymentId());
        order.setRazorpaySignature(request.getRazorpaySignature());
        order.setEstimatedDelivery(LocalDateTime.now().plusMinutes(45));

        // 5. Convert the items from the cart (CartItemDTO) to order items (OrderItemEntity).
        List<OrderItemEntity> orderItems = cartDTO.getItems().stream()
            .map(cartItemDTO -> {
                // Find the corresponding menu item from the database.
                return menuItemRepository.findById(cartItemDTO.getMenuItemId())
                    .map(menuItem -> {
                        OrderItemEntity orderItem = new OrderItemEntity();
                        orderItem.setOrder(order); // Link to the unsaved order
                        orderItem.setMenuItem(menuItem);
                        orderItem.setName(cartItemDTO.getName()); 
                        orderItem.setPrice(cartItemDTO.getPrice()); 
                                            orderItem.setQuantity(cartItemDTO.getQuantity());
                                            orderItem.setImage_url(menuItem.getImage_url()); // Set the image URL
                                            return orderItem;                    })
                    .orElseGet(() -> {
                        log.warn("Menu item ID {} not found while placing order. Skipping item.", cartItemDTO.getMenuItemId());
                        return null;
                    });
            })
            .filter(orderItem -> orderItem != null)
            .collect(Collectors.toList());

        if (orderItems.isEmpty()) {
             throw new IllegalStateException("Cannot place an order with no valid items.");
        }
        
        // 6. Set the items on the order. Because of CascadeType.ALL, these will be saved with the order.
        order.setItems(orderItems);

        // 7. Save the new OrderEntity to the database. This generates the order ID.
        OrderEntity savedOrder = orderRepository.save(order);
        
        // 8. Link each order item to the saved order and then save them.
        orderItems.forEach(item -> item.setOrder(savedOrder));
        ordersItemsRepository.saveAll(orderItems);
        savedOrder.setItems(orderItems);
        
        // 9. Clear the customer's cart now that the order is placed.
        cartService.clearCart(request.getCustomerId());

        // 10. Convert the final OrderEntity to an OrderResponse DTO to send back to the frontend.
        return orderMapper.toResponse(savedOrder);
    }

	public List<OrderResponse> getOrdersHistory(Integer userId) throws DataNotFoundException {
    
    // 1. Verify Customer Existence (Optional, but good practice)
    if (!customerRepository.existsById(userId)) {
        throw new DataNotFoundException("Customer with ID " + userId + " not found.");
    }
    
    // 2. Fetch Orders by User ID, ensuring items are also fetched
    // We use a custom method in OrderRepository for this.
    // Fetching by user is crucial, and sorting by orderDate descending is typical for history.
    List<OrderEntity> orderEntities = orderRepository.findByUserIdOrderByOrderDateDesc(userId);
    
    // NOTE: This list could be empty, which is a valid result (no orders placed yet).

    // 3. Map Entities to DTOs
    List<OrderResponse> orderResponses = orderEntities.stream()
            .map(orderMapper::toResponse)
            .collect(Collectors.toList());
            
    return orderResponses;
}
}