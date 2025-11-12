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

@RestController
@RequestMapping("/api/auth/admin/dashboard")
@CrossOrigin(origins = "http://localhost:4200") 
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    /**
     * Fetches all key metrics and summary data needed to display the Admin dashboard.
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