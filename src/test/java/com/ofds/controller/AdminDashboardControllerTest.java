package com.ofds.controller;

import com.ofds.dto.AdminDashboardDTO;
import com.ofds.service.AdminDashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

/**
 * Vertical Integration Test (WebMvcTest Slice) for AdminDashboardController.
 * This tests the HTTP layer by simulating requests and ensuring the controller 
 * correctly communicates with the service layer and handles JSON responses.
 */
@WebMvcTest(AdminDashboardController.class)
class AdminDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Mock the service layer dependency to isolate the Controller (Vertical Slice)
    @SuppressWarnings({ "removal"})
	@MockBean
    private AdminDashboardService dashboardService;

    // --- Helper DTO ---
    private AdminDashboardDTO getMockDashboardDTO() {
        // Using the DTO's constructor based on AdminDashboardService's implementation
        return new AdminDashboardDTO(
                150L,   // totalCustomers
                20L,    // totalRestaurants
                50L,    // totalAgents
                500L,   // totalOrders
                50L,    // placedOrders
                400L,   // deliveredOrders
                40L,	// totalAvailableAgent
                10L,    // busyAgents
                75500.50 // totalRevenue
        );
    }

    // =========================================================================
    // Test Case for getAdminDashboardMetrics()
    // =========================================================================

    @Test
    void getAdminDashboardMetrics_ShouldReturnMetricsAnd200OK() throws Exception {
        // ARRANGE
        AdminDashboardDTO mockDto = getMockDashboardDTO();
        when(dashboardService.getDashboardData()).thenReturn(mockDto);

        // ACT & ASSERT
        mockMvc.perform(get("/api/admin/dashboard/metrics")
                .contentType(MediaType.APPLICATION_JSON)
                .with(user("admin").roles("ADMIN"))) 
                
                .andExpect(status().isOk()) 
                // ... rest of assertions ...
                .andExpect(jsonPath("$.totalRevenue").value(75500.50));
    }
    
    @Test
    void getAdminDashboardMetrics_ShouldReturnEmptyData_WhenServiceReturnsEmpty() throws Exception {
        // ARRANGE
        AdminDashboardDTO emptyDto = new AdminDashboardDTO(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0.0);
        when(dashboardService.getDashboardData()).thenReturn(emptyDto);

        // ACT & ASSERT
        mockMvc.perform(get("/api/admin/dashboard/metrics")
                .contentType(MediaType.APPLICATION_JSON)
                .with(user("admin").roles("ADMIN"))) 
                
                .andExpect(status().isOk()) 
                .andExpect(jsonPath("$.totalOrders").value(0L))
                .andExpect(jsonPath("$.totalRevenue").value(0.0));
    }
}