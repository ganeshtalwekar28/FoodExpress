import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdminDashboardDTO } from './models/admin-dashboard.model';
import { Orders } from './models/orders';
import { DeliveryAgent } from './models/delivery-agent';

const BACKEND_BASE_URL = 'http://localhost:8082';

const DASHBOARD_API_BASE_URL = `${BACKEND_BASE_URL}/api/auth/admin/dashboard`;
const ORDERS_API_BASE_URL = `${BACKEND_BASE_URL}/api/auth/orders/admin`;
const AGENTS_API_BASE_URL = `${BACKEND_BASE_URL}/api/auth/admin/delivery-agents`;

@Injectable({
    providedIn: 'root'
})
export class AdminService {

    constructor(private http: HttpClient) { }

    // Fetch dashboard metrics
    getDashboardMetrics(): Observable<AdminDashboardDTO> {
        const url = `${DASHBOARD_API_BASE_URL}`;
        console.log('Fetching metrics from:', url);
        return this.http.get<AdminDashboardDTO>(url);
    }

    // Fetch all orders
    getAllOrders(): Observable<Orders[]> {
        const url = `${ORDERS_API_BASE_URL}`;
        console.log('Fetching orders from:', url);
        return this.http.get<Orders[]>(url);
    }

    // Fetch all delivery agents
    getAllAgents(): Observable<DeliveryAgent[]> {
        const url = AGENTS_API_BASE_URL;
        console.log('Fetching ALL agents from:', url);
        return this.http.get<DeliveryAgent[]>(url);
    }

    // Fetch available delivery agents
    getAvailableAgents(): Observable<DeliveryAgent[]> {
        const url = `${ORDERS_API_BASE_URL}/agents/available`;
        console.log('Fetching AVAILABLE agents from:', url);
        return this.http.get<DeliveryAgent[]>(url);
    }

    // assign agent to order
    assignAgent(orderId: number, agentId: number): Observable<any> {
        const payload = {
            orderId: orderId,
            agentId: agentId
        };
        return this.http.put(`${ORDERS_API_BASE_URL}/assign`, payload);
    }
}