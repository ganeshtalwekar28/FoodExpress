package com.ofds.controller;

import com.ofds.config.JwtUtils; // May not be needed if excluded
import com.ofds.dto.AdminDashboardDTO;
import com.ofds.service.AdminDashboardService;
import com.ofds.service.CustomerService; // May not be needed if excluded
import com.ofds.service.CustomerUserDetailsService; // May not be needed if excluded

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@WebMvcTest(
    controllers = AdminDashboardController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class} 
)
class AdminDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminDashboardService dashboardService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private CustomerUserDetailsService customerUserDetailsService;
    
    @MockBean
    private CustomerService customerService;

    private AdminDashboardDTO getMockDashboardDTO() {
        return new AdminDashboardDTO(
                150L, 20L, 50L, 500L, 50L, 400L, 40L, 10L, 75500.50
        );
    }
    
    @Test
    void getAdminDashboardMetrics_ShouldReturnMetricsAnd200OK() throws Exception {
        AdminDashboardDTO mockDto = getMockDashboardDTO();
        when(dashboardService.getDashboardData()).thenReturn(mockDto);

        // ACT & ASSERT: Perform GET request to the correct URI
        mockMvc.perform(get("/api/auth/admin/dashboard") 
                .contentType(MediaType.APPLICATION_JSON)
                .with(user("admin").roles("ADMIN"))) 
                .andExpect(status().isOk()) 
                .andExpect(jsonPath("$.totalRevenue").value(75500.50));
    }
    
    
    @Test
    void getAdminDashboardMetrics_ShouldReturn404NotFound_WhenDataIsNotFound() throws Exception {
        when(dashboardService.getDashboardData()).thenReturn(null);

        // ACT & ASSERT
        mockMvc.perform(get("/api/auth/admin/dashboard")
                .contentType(MediaType.APPLICATION_JSON)
                .with(user("admin").roles("ADMIN"))) 
                .andExpect(status().isNotFound()) // Status is confirmed 404
                .andExpect(content().string("Admin dashboard metrics data could not be retrieved from the service.")); 
    }
}