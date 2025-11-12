package com.ofds.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ofds.dto.AgentAssignmentRequestDTO;
import com.ofds.dto.DeliveryAgentDTO;
import com.ofds.dto.OrderDetailsDTO;
import com.ofds.dto.OrderRequest;
import com.ofds.dto.OrderResponse;
import com.ofds.dto.OrderSummaryDTO;
import com.ofds.entity.DeliveryAgentEntity;
import com.ofds.entity.OrderEntity;
import com.ofds.exception.AgentAssignmentException;
import com.ofds.exception.AgentListNotFoundException;
import com.ofds.exception.DataNotFoundException;
import com.ofds.exception.OrderNotFoundException;
import com.ofds.service.OrdersService;

@RestController
@RequestMapping("/api/auth/orders")
@CrossOrigin(origins = "http://localhost:4200") 
public class OrdersController {

    @Autowired
    private OrdersService ordersService; 

    /**
     * Finalizes the shopping cart and initiates a new food delivery order.
     */
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
    
    /**
     * Retrieves the complete history of orders placed by a specific customer ID.
     */
    @GetMapping("/user/{userID}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUser(@PathVariable Long userID) {
        try {
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
    
	/**
	 * Retrieves a summarized list of all orders currently in the system for the admin dashboard.
	 */
    @GetMapping("/admin")
    public ResponseEntity<List<OrderSummaryDTO>> getAllOrders() throws OrderNotFoundException { 
        List<OrderSummaryDTO> orders = ordersService.findAllOrders();
        if(!orders.isEmpty()) {
        	return new ResponseEntity<>(orders, HttpStatus.OK);
        } else {
        	throw new OrderNotFoundException("No order summaries were found in the system.");
        }
    }

   
    /**
     * Retrieves the detailed information (items, prices, status) for a single order ID.
     */
    @GetMapping("/admin/{orderId}")
    public ResponseEntity<OrderDetailsDTO> getOrderDetails(@PathVariable Long orderId) throws OrderNotFoundException {
        OrderDetailsDTO detailsDto = ordersService.getOrderDetails(orderId);
        if(detailsDto != null) {
        	return new ResponseEntity<>(detailsDto, HttpStatus.OK);
        } else {
        	throw new OrderNotFoundException("Order details not found for ID : " + orderId);
        }
    }

    /**
     * Gets a list of delivery agents who are currently marked as available to take new orders.
     */
    @GetMapping("/admin/agents/available")
    public ResponseEntity<List<DeliveryAgentDTO>> getAvailableDeliveryAgents() throws AgentListNotFoundException {
        List<DeliveryAgentDTO> agents = ordersService.findAvailableDeliveryAgents();
        if(!agents.isEmpty()) {
            return new ResponseEntity<>(agents, HttpStatus.OK);
        } else {
        	throw new AgentListNotFoundException("No available delivery agents found.");
        }
    }
    
    
    /**
     * Manually assigns a specific delivery agent to an unassigned order.
     */
    @PutMapping("/admin/assign")
    public ResponseEntity<Map<String, String>> assignAgentToOrder(@RequestBody AgentAssignmentRequestDTO request) throws AgentAssignmentException {
        OrderEntity updatedOrder = ordersService.assignAgent(request.getOrderId(), request.getAgentId());
        
        // Prepare response with the newly assigned agent's name for UI feedback
        Map<String, String> response = new HashMap<>();
        
        // Ensure the updated order has a valid agent before accessing its name
        if (updatedOrder != null && updatedOrder.getAgent() != null) {
            response.put("agentName", updatedOrder.getAgent().getName()); 
            return ResponseEntity.ok(response);
        } else {
        	throw new AgentAssignmentException("Failed to assign Agent ID " + request.getAgentId() + " to Order ID " + request.getOrderId() + ". Assignment failed or returned null data.");
        }
    }
     
   
    /**
     * Marks a specific order as delivered, updates the order status, and handles agent updates.
     */
    @PutMapping("/admin/{orderId}/deliver")
    public ResponseEntity<Map<String, Object>> markOrderAsDelivered(@PathVariable Long orderId, 
                                                                    @RequestBody Map<String, Object> payload) throws OrderNotFoundException {
        
    	Integer agentIdInt = (Integer) payload.get("agentId");
        Long agentId = agentIdInt != null ? agentIdInt.longValue() : null;
        
        OrderEntity updatedOrder = ordersService.deliverOrder(orderId, agentId);

        // Handle Null Case
        if (updatedOrder == null) {
            throw new OrderNotFoundException("Could not find or update Order ID " + orderId + " for delivery.");
        }
        
        // Prepare simplified response for the front-end
        Map<String, Object> response = new HashMap<>();
        response.put("orderStatus", updatedOrder.getOrderStatus());
        
        // Extract agent status from the updated entity
        DeliveryAgentEntity updatedAgent = updatedOrder.getAgent();
        response.put("agentStatus", updatedAgent != null ? updatedAgent.getStatus() : "Unassigned");
        
        // Order is complete, so there is no current order ID
        response.put("currentOrderID", null); 

        return ResponseEntity.ok(response);
    }
}