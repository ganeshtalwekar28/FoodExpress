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
import com.ofds.mapper.OrderMapper; // Corrected package to .mapper

import com.ofds.repository.CustomerRepository;
import com.ofds.repository.DeliveryAgentRepository;
import com.ofds.repository.MenuItemRepository;
import com.ofds.repository.OrderRepository;
import com.ofds.repository.OrdersItemsRepository;
import com.ofds.repository.RestaurantRepository;

//import jakarta.transaction.Transactional; // Annotation for transaction management

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger; // Needed for logging
import org.slf4j.LoggerFactory; // Needed for logging

// Renaming the class to OrderService is common, but keeping OrdersService is fine.
/**
 * This service contains the business logic for handling orders. It is
 * responsible for placing new orders and retrieving order history.
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

	@Autowired
	DeliveryAgentService deliveryAgentService;
	
	@Autowired
	DeliveryAgentRepository agentRepository;
//    private final MenuItemRepository menuItemRepository;

	/**
	 * Places a new order based on the items in the customer's cart. This method is
	 * transactional, meaning if any step fails, all database changes will be rolled
	 * back.
	 * 
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

		// 2. Create a new OrderEntity. This is the object that will be saved to the
		// 'orders' table.
		OrderEntity order = new OrderEntity();

		// 3. Find the Customer and Restaurant entities from the database using their
		// IDs.
		// We use orElseThrow to handle the case where the customer or restaurant is not
		// found.
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

		log.info("Setting RazorpayOrderId: {}, PaymentId: {}, Signature: {}", request.getRazorpayOrderId(),
				request.getRazorpayPaymentId(), request.getRazorpaySignature());

		order.setRazorpayOrderId(request.getRazorpayOrderId());
		order.setRazorpayPaymentId(request.getRazorpayPaymentId());
		order.setRazorpaySignature(request.getRazorpaySignature());
		order.setEstimatedDelivery(LocalDateTime.now().plusMinutes(45));

		// 5. Convert the items from the cart (CartItemDTO) to order items
		// (OrderItemEntity).
		List<OrderItemEntity> orderItems = cartDTO.getItems().stream().map(cartItemDTO -> {
			// Find the corresponding menu item from the database.
			return menuItemRepository.findById(cartItemDTO.getMenuItemId()).map(menuItem -> {
				OrderItemEntity orderItem = new OrderItemEntity();
				orderItem.setOrder(order); // Link to the unsaved order
				orderItem.setMenuItem(menuItem);
				orderItem.setName(cartItemDTO.getName());
				orderItem.setPrice(cartItemDTO.getPrice());
				orderItem.setQuantity(cartItemDTO.getQuantity());
				orderItem.setImageUrl(menuItem.getImage_url()); // Set the image URL
				return orderItem;
			}).orElseGet(() -> {
				log.warn("Menu item ID {} not found while placing order. Skipping item.", cartItemDTO.getMenuItemId());
				return null;
			});
		}).filter(orderItem -> orderItem != null).collect(Collectors.toList());

		if (orderItems.isEmpty()) {
			throw new IllegalStateException("Cannot place an order with no valid items.");
		}

		// 6. Set the items on the order. Because of CascadeType.ALL, these will be
		// saved with the order.
		order.setItems(orderItems);

		// 7. Save the new OrderEntity to the database. This generates the order ID.
		OrderEntity savedOrder = orderRepository.save(order);

		// 8. Link each order item to the saved order and then save them.
		orderItems.forEach(item -> item.setOrder(savedOrder));
		ordersItemsRepository.saveAll(orderItems);
		savedOrder.setItems(orderItems);

		// 9. Clear the customer's cart now that the order is placed.
		cartService.clearCart(request.getCustomerId());

		// 10. Convert the final OrderEntity to an OrderResponse DTO to send back to the
		// frontend.
		return orderMapper.toResponse(savedOrder);
	}

	public List<OrderResponse> getOrdersHistory(Long userId) throws DataNotFoundException {

		// 1. Verify Customer Existence (Optional, but good practice)
		if (!customerRepository.existsById(userId)) {
			throw new DataNotFoundException("Customer with ID " + userId + " not found.");
		}

		// 2. Fetch Orders by User ID, ensuring items are also fetched
		// We use a custom method in OrderRepository for this.
		// Fetching by user is crucial, and sorting by orderDate descending is typical
		// for history.
		List<OrderEntity> orderEntities = orderRepository.findByUserIdOrderByOrderDateDesc(userId);

		// NOTE: This list could be empty, which is a valid result (no orders placed
		// yet).

		// 3. Map Entities to DTOs
		List<OrderResponse> orderResponses = orderEntities.stream().map(orderMapper::toResponse)
				.collect(Collectors.toList());

		return orderResponses;
	}

	// --------------------------------------------------------------------------------
	// ORDER LIST VIEW METHODS
	// --------------------------------------------------------------------------------

	/**
	 * Retrieves all orders and maps them to OrderSummaryDTOs for the list view.
	 * Uses FETCH JOIN to load all necessary associations eagerly.
	 *
	 * @return A list of OrderSummaryDTOs.
	 */
	@Transactional(readOnly = true)
	public List<OrderSummaryDTO> findAllOrders() {
		return orderRepository.findAllOrdersWithDetails().stream().map(this::mapToOrderSummaryDto)
				.collect(Collectors.toList());
	}

	/**
	 * Maps an OrdersEntity to the flat OrderSummaryDTO structure required by the
	 * Angular list view. * @param order The OrdersEntity to map.
	 * 
	 * @return The populated OrderSummaryDTO.
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
		dto.setCustomerName(order.getUser().getName()); // getCustomer() method to getUser. if any issue occur revert this.
		dto.setDropAddress(order.getDeliveryAddress());
		dto.setRestaurantName(order.getRestaurant().getName());
		dto.setPickupAddress(order.getRestaurant().getAddress());

		// 3. Map Agent Details (Handle unassigned orders)
		if (order.getAgent() != null) {
			dto.setAgentName(order.getAgent().getName());
		} else {
			dto.setAgentName("Unassigned");
		}

		// 4. Map Items and Calculate Total Items count
		List<OrderItemDTO> itemDtos = order.getItemList().stream().map(this::mapToOrderItemDto)
				.collect(Collectors.toList());

		dto.setItems(itemDtos);
		// Total items count is the number of distinct line items in the order
		dto.setTotalItems(itemDtos.size());

		return dto;
	}

	// --------------------------------------------------------------------------------
	// ORDER DETAIL VIEW METHODS
	// --------------------------------------------------------------------------------

	/**
	 * Retrieves detailed information for a single order, including all items and a
	 * list of currently available delivery agents.
	 *
	 * @param orderId The ID of the order to retrieve.
	 * @return The populated OrdersDetailsDTO.
	 * @throws OrderNotFoundException if the order ID does not exist.
	 */
	@Transactional(readOnly = true)
	public OrderDetailsDTO getOrderDetails(Long orderId) {
		// FindByIdWithItems uses a FETCH JOIN for OrderItems and relies on other joins
		// for Customer/Restaurant.
		OrderEntity order = orderRepository.findByIdWithItems(orderId)
				.orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

		// Map OrderItemsEntity list to OrdersItemsDTO list
		List<OrderItemDTO> itemDtos = order.getItems().stream().map(this::mapToOrderItemDto)
				.collect(Collectors.toList());

		// Fetch list of available delivery agents (delegated to DeliveryAgentService)
		List<DeliveryAgentDTO> agentDtos = deliveryAgentService.findAvailableDeliveryAgents();

		// Map the main entity data to the DTO
		return mapToOrderDetailsDto(order, itemDtos, agentDtos);
	}

	/**
	 * Maps an OrderItemsEntity to its corresponding OrdersItemsDTO, fetching the
	 * menu item name via the repository.
	 */
	private OrderItemDTO mapToOrderItemDto(OrderItemEntity item) {
		
		return new OrderItemDTO (
			item.getName(),
			item.getPrice(),
			item.getQuantity()
		);
//		Long menuItemId = item.getMenuItem();
//
//		// Fetch the name using the injected repository
//		String itemName = menuItemRepository.findById(menuItemId)
//				.map(MenuItemEntity::getName)
//				.orElse("Unknown Item (ID: " + menuItemId + ")");
//
//		return new OrderItemDTO(
//			item.getName()
//			item.getPrice(), 
//			item.getQuantity(), 
//			item.getImage_url()
//		);
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
		dto.setCustomerAddress(order.getDeliveryAddress()); // Used for drop address

		dto.setRestaurantName(order.getRestaurant().getName());
		dto.setRestaurantAddress(order.getRestaurant().getAddress()); // Used for pickup address

		dto.setItems(itemDtos);
		dto.setAvailableAgents(agentDtos);

		if (order.getAgent() != null) {
			dto.setAgentName(order.getAgent().getName());
		}

		return dto;
	}

	/**
	 * Finds all available delivery agents (delegates to DeliveryAgentService).
	 */
	@Transactional(readOnly = true)
	public List<DeliveryAgentDTO> findAvailableDeliveryAgents() {
		return deliveryAgentService.findAvailableDeliveryAgents();
	}

	// --------------------------------------------------------------------------------
	// ORDER ASSIGNMENT METHODS
	// --------------------------------------------------------------------------------

	/**
	 * Assigns a specified agent to a placed order.
	 *
	 * @param orderId The ID of the order to assign.
	 * @param agentId The ID of the agent to assign.
	 * @return The updated OrdersEntity.
	 * @throws OrderNotFoundException     if the order is not found.
	 * @throws AgentListNotFoundException if the agent is not found.
	 * @throws AgentAssignmentException   if the order/agent status prevents
	 *                                    assignment.
	 */
	@Transactional
	public OrderEntity assignAgent(Long orderId, Long agentId) {
		// Fetch entities, throwing custom exceptions if not found
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

		// 2. Update the Agent status (Agent is now BUSY)
		agent.setStatus("BUSY");
		agentRepository.save(agent);

		return updatedOrder;
	}

	// --------------------------------------------------------------------------------
	// ORDER DELIVERY METHODS
	// --------------------------------------------------------------------------------

	/**
	 * Marks an order as delivered, updates the associated agent's statistics
	 * (including commission), and sets the agent's status back to AVAILABLE.
	 *
	 * @param orderId The ID of the order being delivered.
	 * @param agentId The ID of the agent who delivered it (for
	 *                verification/safety).
	 * @return The updated OrdersEntity.
	 * @throws OrderNotFoundException if the order is not found.
	 */
	@Transactional
	public OrderEntity deliverOrder(Long orderId, Long agentId) {
		// Fetch order, throwing custom exception if not found
		OrderEntity order = orderRepository.findById(orderId)
				.orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

		// 1. Mark delivered
		order.setOrderStatus("DELIVERED");

		// 2. Update agent statistics and status
		DeliveryAgentEntity agent = order.getAgent();

		// Only update agent stats if an agent is properly linked
		if (agent != null) {
			double total = order.getTotalAmount() == null ? 0.0 : order.getTotalAmount();

			// Calculate 15% commission on the order total
			double rawBonus = total * 0.15;

			// Round the bonus to 2 decimal places (cents) for financial accuracy
			double roundedBonus = Math.round(rawBonus * 100.0) / 100.0;

			// Update earnings and delivery count
			agent.setTodaysEarning((agent.getTodaysEarning() == null ? 0.0 : agent.getTodaysEarning()) + roundedBonus);
			agent.setTotalEarnings((agent.getTotalEarnings() == null ? 0.0 : agent.getTotalEarnings()) + roundedBonus);
			agent.setTotalDeliveries((agent.getTotalDeliveries() == null ? 0 : agent.getTotalDeliveries()) + 1);

			// Agent is now AVAILABLE after completing the delivery
			agent.setStatus("AVAILABLE");

			agentRepository.save(agent);
		} else {
			System.err.println(
					"Warning: Order ID " + orderId + " delivered, but no agent was linked to update earnings.");
		}

		return orderRepository.save(order);
	}
}