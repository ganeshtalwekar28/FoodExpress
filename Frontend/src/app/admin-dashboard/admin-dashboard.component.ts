import { Component, OnInit } from '@angular/core';
import { AdminService } from '../admin.service';
import { AdminDashboardDTO } from '../models/admin-dashboard.model';

@Component({
  selector: 'app-admin-dashboard',
  standalone: false,
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.css'
})
export class AdminDashboardComponent implements OnInit {

  totalCustomers: number = 0;
  totalRestaurants: number = 0;

  totalAgents: number = 0;
  busyAgents: number = 0;
  
  availableAgents: number = 0; 

  totalOrders: number = 0;
  placedOrders: number = 0;
  deliveredOrders: number = 0;
  
  totalRevenue: number = 0;
  
  isLoading: boolean = false;
  error: string | null = null;
  
  constructor(private adminService: AdminService) { }

  // Lifecycle hook to initialize component
  ngOnInit(): void {
    this.fetchMetrics();
  }
  
  private fetchMetrics(): void {
    this.isLoading = true;
    this.error = null; 

    // Fetch dashboard metrics from the service
    this.adminService.getDashboardMetrics().subscribe({
      next: (data: AdminDashboardDTO) => {
        
        this.totalCustomers = data.totalCustomers;
        this.totalRestaurants = data.totalRestaurants;
        
        this.totalOrders = data.totalOrders;
        this.placedOrders = data.placedOrders;
        this.deliveredOrders = data.deliveredOrders;     
        
        this.totalAgents = data.totalDeliveryAgents;
        this.busyAgents = data.busyAgents;
        
        this.availableAgents = this.totalAgents - this.busyAgents;
        
        this.totalRevenue = data.totalRevenue;

        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching dashboard metrics:', err);
        this.error = 'Failed to load data. Check API status and CORS configuration.';
        this.isLoading = false;

        this.totalCustomers = 0;
        this.totalRestaurants = 0;
        this.totalAgents = 0;
        this.busyAgents = 0;
        this.availableAgents = 0;
        this.totalOrders = 0;
        this.placedOrders = 0;
        this.deliveredOrders = 0;
        this.totalRevenue = 0;
      }
    });
  }
}