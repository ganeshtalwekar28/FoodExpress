package com.ofds.service;

import com.ofds.dto.AdminDashboardDTO;
import com.ofds.repository.CustomerRepository;
import com.ofds.repository.DeliveryAgentRepository;
import com.ofds.repository.OrderRepository;
import com.ofds.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service layer responsible for aggregating various metrics (counts and sums)
 * across different repositories to populate the Admin Dashboard.
 */
@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final DeliveryAgentRepository agentRepository;
    private final OrderRepository ordersRepository;

    /**
     * Retrieves all core metrics and counts required for the Admin Dashboard.
     * The data is compiled using simple aggregate operations (count, sum).
     *
     * @return An AdminDashboardDTO containing all computed metrics.
     */
    public AdminDashboardDTO getDashboardData() {

        // Status constants should ideally be defined in an external constants file.
        final String STATUS_PLACED = "PLACED";
        final String STATUS_DELIVERED = "DELIVERED";
        final String AGENT_STATUS_BUSY = "BUSY"; // Using uppercase standard for consistency
        final String AGENT_STATUS_AVAILABLE = "AVAILABLE";

        // --- 1. Fetch Core Counts (Total Entities) ---
        long totalCustomers = customerRepository.count();
        long totalRestaurants = restaurantRepository.count();
        long totalAgents = agentRepository.count();
        long totalOrders = ordersRepository.count();

        // --- 2. Fetch Status-based Counts (Operational Metrics) ---
        // These rely on custom repository methods (e.g., countByStatus(String status))
        long placedOrders = ordersRepository.countByOrderStatus(STATUS_PLACED);
        long deliveredOrders = ordersRepository.countByOrderStatus(STATUS_DELIVERED);
        long busyAgents = agentRepository.countByStatus(AGENT_STATUS_BUSY);
        long totalAvailableAgent = agentRepository.countByStatus(AGENT_STATUS_AVAILABLE);

        // --- 3. Fetch Aggregate (Financial Metrics) ---
        // Sums the total amount of all orders marked as DELIVERED.
        // Returns null if no delivered orders exist, so we default to 0.0.
        Double totalRevenue = ordersRepository.sumTotalAmountByStatusDelivered();

        // Create the DTO using the all-arguments constructor
        return new AdminDashboardDTO(
                totalCustomers,
                totalRestaurants,
                totalAgents,
                totalOrders,
                placedOrders,
                deliveredOrders,
                totalAvailableAgent,
                busyAgents,
                // Ensures totalRevenue is 0.0 if the repository returns null
                totalRevenue != null ? totalRevenue : 0.0
        );
    }
}