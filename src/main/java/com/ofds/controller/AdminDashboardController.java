package com.ofds.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ofds.dto.AdminDashboardDTO;
import com.ofds.exception.MetricsDataNotFound;
import com.ofds.service.AdminDashboardService;

/**
 * REST controller for retrieving administrative dashboard metrics.
 * This provides aggregated summary data for the main admin console view.
 */
@RestController
@RequestMapping("/api/auth/admin/dashboard")
@CrossOrigin(origins = "http://localhost:4200") 
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    /**
     * Endpoint to fetch a collection of key performance metrics for the admin dashboard.
     * The service layer handles data aggregation.
     *
     * @return A ResponseEntity containing the AdminDashboardDTO with status 200 OK.
     */
    @GetMapping()
    public ResponseEntity<AdminDashboardDTO> getAdminDashboardMetrics() throws MetricsDataNotFound {
        AdminDashboardDTO metrics = dashboardService.getDashboardData();

        if (metrics != null) {
            return new ResponseEntity<>(metrics, HttpStatus.OK);
        } else {
        	throw new MetricsDataNotFound("Admin dashboard metrics data could not be retrieved from the service.");
        }
    }
}