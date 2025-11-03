package com.ofds.service;

import com.ofds.dto.AdminDashboardDTO;
import com.ofds.repository.CustomerRepository;
import com.ofds.repository.DeliveryAgentRepository;
import com.ofds.repository.OrderRepository;
import com.ofds.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit Test class for AdminDashboardService.
 * Focuses on isolating and testing the service's aggregation logic and DTO mapping
 * by mocking all repository count/sum methods. (Horizontal Testing)
 */
@ExtendWith(MockitoExtension.class)
class AdminDashboardServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private DeliveryAgentRepository agentRepository;

    @Mock
    private OrderRepository ordersRepository;

    @InjectMocks
    private AdminDashboardService adminDashboardService;

    // --- Constants for Status Verification ---
    private final String STATUS_PLACED = "PLACED";
    private final String STATUS_DELIVERED = "DELIVERED";
    private final String AGENT_STATUS_BUSY = "BUSY";

    @BeforeEach
    void setUp() {
        // No complex setup needed; we mock all repository method calls directly in tests.
    }

    // =========================================================================
    // Test Cases for getDashboardData() - Happy Path
    // =========================================================================

    @Test
    void getDashboardData_ShouldReturnCorrectMetrics_WhenAllDataExists() {
        // ARRANGE
        // 1. Core Counts
        when(customerRepository.count()).thenReturn(150L);
        when(restaurantRepository.count()).thenReturn(20L);
        when(agentRepository.count()).thenReturn(50L);
        when(ordersRepository.count()).thenReturn(500L);

        // 2. Status-based Counts
        when(ordersRepository.countByOrderStatus(STATUS_PLACED)).thenReturn(50L);
        when(ordersRepository.countByOrderStatus(STATUS_DELIVERED)).thenReturn(400L);
        when(agentRepository.countByStatus(AGENT_STATUS_BUSY)).thenReturn(10L);

        // 3. Financial Metric
        when(ordersRepository.sumTotalAmountByStatusDelivered()).thenReturn(75500.50);

        // ACT
        AdminDashboardDTO result = adminDashboardService.getDashboardData();

        // ASSERT
        assertNotNull(result);

        // Assert Core Counts
        assertEquals(150L, result.getTotalCustomers());
        assertEquals(20L, result.getTotalRestaurants());
        assertEquals(50L, result.getTotalDeliveryAgents());
        assertEquals(500L, result.getTotalOrders());

        // Assert Status-based Counts
        assertEquals(50L, result.getPlacedOrders());
        assertEquals(400L, result.getDeliveredOrders());
        assertEquals(10L, result.getBusyAgents());

        // Assert Financial Metric
        assertEquals(75500.50, result.getTotalRevenue(), 0.001);

        // Verify all mocked methods were called exactly once
        verify(customerRepository, times(1)).count();
        verify(restaurantRepository, times(1)).count();
        verify(agentRepository, times(1)).count();
        verify(ordersRepository, times(1)).count();
        verify(ordersRepository, times(1)).countByOrderStatus(STATUS_PLACED);
        verify(ordersRepository, times(1)).countByOrderStatus(STATUS_DELIVERED);
        verify(agentRepository, times(1)).countByStatus(AGENT_STATUS_BUSY);
        verify(ordersRepository, times(1)).sumTotalAmountByStatusDelivered();
    }

    // =========================================================================
    // Test Cases for Edge Cases (Null/Zero Values)
    // =========================================================================

    @Test
    void getDashboardData_ShouldHandleZeroCountsCorrectly() {
        // ARRANGE
        // Mock all counts to zero
        when(customerRepository.count()).thenReturn(0L);
        when(restaurantRepository.count()).thenReturn(0L);
        when(agentRepository.count()).thenReturn(0L);
        when(ordersRepository.count()).thenReturn(0L);
        when(ordersRepository.countByOrderStatus(anyString())).thenReturn(0L);
        
        // Mock revenue to return null (which is common for SUM() on empty sets)
        when(ordersRepository.sumTotalAmountByStatusDelivered()).thenReturn(null);

        // ACT
        AdminDashboardDTO result = adminDashboardService.getDashboardData();

        // ASSERT
        assertNotNull(result);
        
        // Assert all counts are 0L
        assertEquals(0L, result.getTotalCustomers());
        assertEquals(0L, result.getTotalRestaurants());
        assertEquals(0L, result.getTotalDeliveryAgents());
        assertEquals(0L, result.getTotalOrders());
        assertEquals(0L, result.getPlacedOrders());
        assertEquals(0L, result.getDeliveredOrders());
        assertEquals(0L, result.getBusyAgents());
        
        // Assert that the total revenue defaults to 0.0 when the repository returns null
        assertEquals(0.0, result.getTotalRevenue(), "Total Revenue should default to 0.0 when repository returns null.");
    }
    
    @Test
    void getDashboardData_ShouldHandleNullRevenue_WhenNoDeliveredOrdersExist() {
        // ARRANGE
        // Provide some valid counts (stubbing all 'count' and 'countByStatus' calls)
        when(customerRepository.count()).thenReturn(1L);
        when(restaurantRepository.count()).thenReturn(1L);
        when(agentRepository.count()).thenReturn(1L);
        
        when(ordersRepository.count()).thenReturn(10L);
        
        // --- STUB THE MISSING CALL HERE ---
        when(ordersRepository.countByOrderStatus(STATUS_PLACED)).thenReturn(10L); 
        
        // Stub the intended part of the test (0 delivered orders)
        when(ordersRepository.countByOrderStatus(STATUS_DELIVERED)).thenReturn(0L); 
        
        // Mock revenue to return null
        when(ordersRepository.sumTotalAmountByStatusDelivered()).thenReturn(null);

        // ACT
        AdminDashboardDTO result = adminDashboardService.getDashboardData();

        // ASSERT
        assertNotNull(result);
        assertEquals(10L, result.getTotalOrders());
        assertEquals(0L, result.getDeliveredOrders());
        // Crucial check: Ensure the service logic handles the null gracefully
        assertEquals(0.0, result.getTotalRevenue(), "Total Revenue must be 0.0 when sumTotalAmountByStatusDelivered returns null.");
        
        // Verify the calls
        verify(ordersRepository, times(1)).countByOrderStatus(STATUS_PLACED); // Verify the previously unstubbed call
        verify(ordersRepository, times(1)).sumTotalAmountByStatusDelivered();
    }
}