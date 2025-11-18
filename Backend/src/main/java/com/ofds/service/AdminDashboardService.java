package com.ofds.service;

import com.ofds.dto.AdminDashboardDTO;
import com.ofds.repository.CustomerRepository;
import com.ofds.repository.DeliveryAgentRepository;
import com.ofds.repository.OrderRepository;
import com.ofds.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for calculating and aggregating key operational, entity, and financial metrics 
 * for the Admin Dashboard view.
 */
@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final DeliveryAgentRepository agentRepository;
    private final OrderRepository ordersRepository;

    /**
     * Retrieves all core metrics and counts required for the Admin Dashboard DTO.
     */
    public AdminDashboardDTO getDashboardData() {

        final String STATUS_PLACED = "PLACED";
        final String STATUS_DELIVERED = "DELIVERED";
        final String AGENT_STATUS_BUSY = "BUSY";
        final String AGENT_STATUS_AVAILABLE = "AVAILABLE";

        long totalCustomers = customerRepository.count();
        long totalRestaurants = restaurantRepository.count();
        long totalAgents = agentRepository.count();
        long totalOrders = ordersRepository.count();

        long placedOrders = ordersRepository.countByOrderStatus(STATUS_PLACED);
        long deliveredOrders = ordersRepository.countByOrderStatus(STATUS_DELIVERED);
        long busyAgents = agentRepository.countByStatus(AGENT_STATUS_BUSY);
        long totalAvailableAgent = agentRepository.countByStatus(AGENT_STATUS_AVAILABLE);

        Double totalRevenue = ordersRepository.sumTotalAmountByStatusDelivered();

        return new AdminDashboardDTO(
                totalCustomers,
                totalRestaurants,
                totalAgents,
                totalOrders,
                placedOrders,
                deliveredOrders,
                totalAvailableAgent,
                busyAgents,
                totalRevenue != null ? totalRevenue : 0.0
        );
    }
}