package com.ofds.service;

import com.ofds.dto.CartDTO;
import com.ofds.dto.DeliveryAgentDTO;
import com.ofds.dto.OrderDetailsDTO;
import com.ofds.dto.OrderItemDTO;
import com.ofds.dto.OrderRequest;
import com.ofds.dto.OrderResponse;
import com.ofds.dto.OrderSummaryDTO;
import com.ofds.entity.CustomerEntity;
import com.ofds.entity.DeliveryAgentEntity;
import com.ofds.entity.OrderEntity;
import com.ofds.entity.OrderItemEntity;
import com.ofds.entity.RestaurantEntity;
import com.ofds.exception.AgentAssignmentException;
import com.ofds.exception.AgentListNotFoundException;
import com.ofds.exception.DataNotFoundException;
import com.ofds.exception.OrderNotFoundException;
import com.ofds.mapper.OrderMapper; 

import com.ofds.repository.CustomerRepository;
import com.ofds.repository.DeliveryAgentRepository;
import com.ofds.repository.MenuItemRepository;
import com.ofds.repository.OrderRepository;
import com.ofds.repository.OrdersItemsRepository;
import com.ofds.repository.RestaurantRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger; 
import org.slf4j.LoggerFactory; 

/**
 * Service class responsible for all business logic related to order processing, 
 * including placement, history retrieval, assignment, and delivery management.
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
	private MenuItemRepository menuItemRepository; 

	@Autowired
	OrderMapper orderMapper;

	@Autowired
	DeliveryAgentService deliveryAgentService;
	
	@Autowired
	DeliveryAgentRepository agentRepository;

	/**
	 * Places a new order based on the items in the customer's cart. The process is transactional.
	 */
	@Transactional
	public OrderResponse placeOrder(OrderRequest request) throws DataNotFoundException {
		log.info("OrderRequest received: {}", request);

		// 1. Fetch the customer's cart to get the items for the order.
		CartDTO cartDTO = cartService.getCartByCustomerId(request.getCustomerId());

		if (cartDTO == null || cartDTO.getItems().isEmpty()) {
			throw new IllegalStateException("Cannot place an order with an empty cart or non-existent cart.");
		}

		// 2. Create a new OrderEntity.
		OrderEntity order = new OrderEntity();

		// 3. Find the Customer and Restaurant entities.
		CustomerEntity customer = customerRepository.findById(request.getCustomerId())
				.orElseThrow(() -> new DataNotFoundException("Customer not found"));

		RestaurantEntity restaurant = restaurantRepository.findById(cartDTO.getRestaurantId())
				.orElseThrow(() -> new DataNotFoundException("Restaurant not found"));

		// 4. Map the details from the request and cart to the OrderEntity.
		order.setUser(customer);
		order.setUserId(customer.getId()); 
		order.setRestaurant(restaurant);
		order.setOrderDate(LocalDateTime.now());
		order.setOrderStatus("Placed");
		order.setPaymentStatus("Paid"); 
		order.setPaymentMethod("Razorpay"); 
		order.setTotalAmount(request.getTotalAmount());
		order.setDeliveryAddress(request.getDeliveryAddress());

		log.info("Setting RazorpayOrderId: {}, PaymentId: {}, Signature: {}", request.getRazorpayOrderId(),
				request.getRazorpayPaymentId(), request.getRazorpaySignature());

		order.setRazorpayOrderId(request.getRazorpayOrderId());
		order.setRazorpayPaymentId(request.getRazorpayPaymentId());
		order.setRazorpaySignature(request.getRazorpaySignature());
		order.setEstimatedDelivery(LocalDateTime.now().plusMinutes(45));

		// 5. Convert the items from the cart to order items.
		List<OrderItemEntity> orderItems = cartDTO.getItems().stream().map(cartItemDTO -> {
			
			return menuItemRepository.findById(cartItemDTO.getMenuItemId()).map(menuItem -> {
				OrderItemEntity orderItem = new OrderItemEntity();
				orderItem.setOrder(order); 
				orderItem.setMenuItem(menuItem);
				orderItem.setName(cartItemDTO.getName());
				orderItem.setPrice(cartItemDTO.getPrice());
				orderItem.setQuantity(cartItemDTO.getQuantity());
				orderItem.setImage_url(menuItem.getImage_url()); 
				return orderItem;
			}).orElseGet(() -> {
				log.warn("Menu item ID {} not found while placing order. Skipping item.", cartItemDTO.getMenuItemId());
				return null;
			});
		}).filter(orderItem -> orderItem != null).collect(Collectors.toList());

		if (orderItems.isEmpty()) {
			throw new IllegalStateException("Cannot place an order with no valid items.");
		}

		// 6. Set the items on the order.
		order.setItems(orderItems);

		// 7. Save the new OrderEntity.
		OrderEntity savedOrder = orderRepository.save(order);

		// 8. Link each order item to the saved order and then save them.
		orderItems.forEach(item -> item.setOrder(savedOrder));
		ordersItemsRepository.saveAll(orderItems);
		savedOrder.setItems(orderItems);

		// 9. Clear the customer's cart.
		cartService.clearCart(request.getCustomerId());

		// 10. Convert the final OrderEntity to an OrderResponse DTO.
		return orderMapper.toResponse(savedOrder);
	}

	/**
	 * Retrieves the historical list of orders for a specific user ID.
	 */
	public List<OrderResponse> getOrdersHistory(Long userId) throws DataNotFoundException {

		if (!customerRepository.existsById(userId)) {
			throw new DataNotFoundException("Customer with ID " + userId + " not found.");
		}

		List<OrderEntity> orderEntities = orderRepository.findByUserIdOrderByOrderDateDesc(userId);

		List<OrderResponse> orderResponses = orderEntities.stream().map(orderMapper::toResponse)
				.collect(Collectors.toList());

		return orderResponses;
	}

	/**
	 * Retrieves all orders and maps them to OrderSummaryDTOs for the admin list view.
	 */
	@Transactional(readOnly = true)
	public List<OrderSummaryDTO> findAllOrders() {
		return orderRepository.findAllOrdersWithDetails().stream().map(this::mapToOrderSummaryDto)
				.collect(Collectors.toList());
	}

	/**
	 * Maps an OrderEntity to the flat OrderSummaryDTO structure.
	 */
	private OrderSummaryDTO mapToOrderSummaryDto(OrderEntity order) {
		OrderSummaryDTO dto = new OrderSummaryDTO();

		// 1. Map Core Order Details
		dto.setId(order.getId());
		dto.setOrderID(order.getId());
		dto.setStatus(order.getOrderStatus());
		dto.setTotalAmount(order.getTotalAmount());
		dto.setOrderDate(order.getOrderDate());

		// 2. Map Flattened Customer and Restaurant Details
		dto.setCustomerName(order.getUser().getName()); 
		dto.setDropAddress(order.getDeliveryAddress());
		dto.setRestaurantName(order.getRestaurant().getName());
		dto.setPickupAddress(order.getRestaurant().getAddress());

		// 3. Map Agent Details
		if (order.getAgent() != null) {
			dto.setAgentName(order.getAgent().getName());
		} else {
			dto.setAgentName("Unassigned");
		}

		// 4. Map Items and Calculate Total Items count
		List<OrderItemDTO> itemDtos = order.getItemList().stream().map(this::mapToOrderItemDto)
				.collect(Collectors.toList());

		dto.setItems(itemDtos);
		dto.setTotalItems(itemDtos.size());

		return dto;
	}
	
	/**
	 * Retrieves detailed information for a single order, including all items and available agents.
	 */
	@Transactional(readOnly = true)
	public OrderDetailsDTO getOrderDetails(Long orderId) {
		
		OrderEntity order = orderRepository.findByIdWithItems(orderId)
				.orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

		List<OrderItemDTO> itemDtos = order.getItems().stream().map(this::mapToOrderItemDto)
				.collect(Collectors.toList());

		List<DeliveryAgentDTO> agentDtos = deliveryAgentService.findAvailableDeliveryAgents();

		return mapToOrderDetailsDto(order, itemDtos, agentDtos);
	}

	/**
	 * Maps an OrderItemEntity to its corresponding OrderItemDTO.
	 */
	private OrderItemDTO mapToOrderItemDto(OrderItemEntity item) {
		
		return new OrderItemDTO (
			item.getName(),
			item.getPrice(),
			item.getQuantity()
		);
	}

	/**
	 * Maps the core order entity data and associated lists into the detail DTO.
	 */
	private OrderDetailsDTO mapToOrderDetailsDto(OrderEntity order, List<OrderItemDTO> itemDtos,
			List<DeliveryAgentDTO> agentDtos) {

		OrderDetailsDTO dto = new OrderDetailsDTO();

		dto.setOrderId(order.getId());
		dto.setOrderStatus(order.getOrderStatus());
		dto.setTotalAmount(order.getTotalAmount());

		dto.setCustomerName(order.getUser().getName());
		dto.setCustomerAddress(order.getDeliveryAddress()); 

		dto.setRestaurantName(order.getRestaurant().getName());
		dto.setRestaurantAddress(order.getRestaurant().getAddress()); 

		dto.setItems(itemDtos);
		dto.setAvailableAgents(agentDtos);

		if (order.getAgent() != null) {
			dto.setAgentName(order.getAgent().getName());
		}

		return dto;
	}

	/**
	 * Finds all available delivery agents.
	 */
	@Transactional(readOnly = true)
	public List<DeliveryAgentDTO> findAvailableDeliveryAgents() {
		return deliveryAgentService.findAvailableDeliveryAgents();
	}

	
	/**
	 * Assigns a specific delivery agent to a PLACED order and updates statuses (Order: OUT FOR DELIVERY, Agent: BUSY).
	 */
	@Transactional
	public OrderEntity assignAgent(Long orderId, Long agentId) {
		// Fetch entities
		OrderEntity order = orderRepository.findById(orderId)
				.orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

		DeliveryAgentEntity agent = agentRepository.findById(agentId)
				.orElseThrow(() -> new AgentListNotFoundException("Delivery Agent not found with ID: " + agentId));

		// Business rule validation
		if (!("PLACED".equals(order.getOrderStatus()) || "Placed".equals(order.getOrderStatus()))) {
			throw new AgentAssignmentException(
					"Order ID " + orderId + " is not in PLACED status and cannot be assigned.");
		}

		if (!"AVAILABLE".equals(agent.getStatus())) {
			throw new AgentAssignmentException("Delivery Agent ID " + agentId + " is not AVAILABLE.");
		}

		// 1. Update the Order
		order.setAgent(agent);
		order.setOrderStatus("OUT FOR DELIVERY");
		OrderEntity updatedOrder = orderRepository.save(order);

		// 2. Update the Agent status
		agent.setStatus("BUSY");
		agentRepository.save(agent);

		return updatedOrder;
	}


	/**
	 * Marks an order as DELIVERED, updates the agent's statistics and total earnings, and sets the agent back to AVAILABLE.
	 */
	@Transactional
	public OrderEntity deliverOrder(Long orderId, Long agentId) {
		// Fetch order
		OrderEntity order = orderRepository.findById(orderId)
				.orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

		// 1. Mark delivered
		order.setOrderStatus("DELIVERED");

		// 2. Update agent statistics and status
		DeliveryAgentEntity agent = order.getAgent();

		if (agent != null) {
			double total = order.getTotalAmount() == null ? 0.0 : order.getTotalAmount();

			// Calculate 15% commission on the order total
			double rawBonus = total * 0.15;

			// Round the bonus to 2 decimal places
			double roundedBonus = Math.round(rawBonus * 100.0) / 100.0;

			// Update earnings and delivery count
			agent.setTodaysEarning((agent.getTodaysEarning() == null ? 0.0 : agent.getTodaysEarning()) + roundedBonus);
			agent.setTotalEarnings((agent.getTotalEarnings() == null ? 0.0 : agent.getTotalEarnings()) + roundedBonus);
			agent.setTotalDeliveries((agent.getTotalDeliveries() == null ? 0 : agent.getTotalDeliveries()) + 1);

			// Agent is now AVAILABLE
			agent.setStatus("AVAILABLE");

			agentRepository.save(agent);
		} else {
			System.err.println(
					"Warning: Order ID " + orderId + " delivered, but no agent was linked to update earnings.");
		}

		return orderRepository.save(order);
	}
}