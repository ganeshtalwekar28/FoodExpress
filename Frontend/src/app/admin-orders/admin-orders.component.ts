import { Component, OnInit } from '@angular/core';
import { AdminService } from '../admin.service';
import { Orders } from '../models/orders';
import { DeliveryAgent } from '../models/delivery-agent';

interface AgentAssignmentResponse {
    success: string;
    message: string;
    agentName: string;
    orderId?: number;
}

@Component({
    selector: 'app-admin-orders',
    standalone: false,
    templateUrl: './admin-orders.component.html',
    styleUrl: './admin-orders.component.css'
})

export class AdminOrdersComponent implements OnInit {

    assignmentBanner = {
        show: false,
        agentName: '',
        orderID: 0
    };

    isLoading: boolean = true;
    error: string | null = null;
    agentError: string | null = null;

    activeTab: 'placed' | 'assigned' | 'delivered' = 'placed';

    orders: Orders[] = [];
    agents: DeliveryAgent[] = [];

    placedOrders: Orders[] = [];
    assignedOrders: Orders[] = [];
    deliveredOrders: Orders[] = [];

    availableAgents: DeliveryAgent[] = [];
    selectedAgent: { [orderID: number]: DeliveryAgent | null } = {};
    isAssigning: { [orderId: number]: boolean } = {};

    constructor(private adminService: AdminService) { }

    ngOnInit(): void {
        this.fetchAllData();
    }

    // Switch active tab
    setActiveTab(tab: 'placed' | 'assigned' | 'delivered'): void {
        this.activeTab = tab;
    }

    // Get orders for the current tab
    getOrdersForTab(tab: 'placed' | 'assigned' | 'delivered'): Orders[] {
        switch (tab) {
            case 'placed':
                return this.placedOrders;
            case 'assigned':
                return this.assignedOrders;
            case 'delivered':
                return this.deliveredOrders;
            default:
                return [];
        }
    }

    // Get Bootstrap color class based on order status
    getStatusColor(status: string): string {
        switch (this.normalizeStatus(status)) {
            case 'placed': 
                return 'warning';
            case 'out for delivery':
                return 'primary';
            case 'delivered': 
                return 'success';
            default: return 'secondary';
        }
    }

    // Normalize status strings for consistent comparison
    private normalizeStatus(s?: string): string {
        return (s || '').toString().trim().toLowerCase().replace(/_/g, ' ');
    }

    // Fetch all orders and available agents
    private fetchAllData(): void {
        this.isLoading = true;
        this.error = null;

        // Fetch orders
        this.adminService.getAllOrders().subscribe({
            next: (data: Orders[]) => {
                this.orders = data;
                this.filterAndSortOrders(data);
            },
            error: (err) => {
                console.error('Error fetching orders:', err);
                this.error = 'Failed to load orders.';
            },
            complete: () => {
                this.isLoading = false;
            }
        });

        // Fetch available agents
        this.adminService.getAvailableAgents().subscribe({
            next: (data: DeliveryAgent[]) => {
                this.availableAgents = data.filter(a => this.normalizeStatus(a.status) === 'available');
                this.agentError = null;
            },
            error: (err) => {
                console.error('Error fetching agents:', err);
                this.agentError = 'Failed to load available agents.';
            }
        });
    }

    // Filter and sort orders into categories
    private filterAndSortOrders(data: Orders[]): void {
        const sortFn = (a: Orders, b: Orders) => new Date(b.orderDate as string | Date).getTime() - new Date(a.orderDate as string | Date).getTime();

        this.placedOrders = data
            .filter(o => this.normalizeStatus(o.status) === 'placed')
            .sort(sortFn);

        this.assignedOrders = data
            .filter(o => this.normalizeStatus(o.status) === 'out for delivery')
            .sort(sortFn);

        this.deliveredOrders = data
            .filter(o => this.normalizeStatus(o.status) === 'delivered')
            .sort(sortFn);
    }

    // Assign selected agent to the order
    assignAgent(orderID: number): void {
        const selectedAgent = this.selectedAgent[orderID];
        const order = this.orders.find(o => o.orderID === orderID);

        if (!selectedAgent || !order || this.isAssigning[orderID]) {
            if (!selectedAgent) { console.error('No agent selected.'); }
            return;
        }

        this.isAssigning[orderID] = true; 
        this.error = null; 

        const assignmentPayload = {
            orderId: orderID,
            agentsId: selectedAgent.id
        };

        this.adminService.assignAgent(assignmentPayload.orderId, assignmentPayload.agentsId).subscribe({
            next: (response: any) => {
                this.fetchAllData();

                this.showAssignmentSuccess(orderID, response.agentName || selectedAgent.name || 'Unknown Agent');
                this.isAssigning[orderID] = false;
            },
            error: (err) => {
                console.error('Assignment failed:', err);
                this.error = `Failed to assign agent to Order #${orderID}. Error: ${err.message || 'Server connection failed.'}`;
                this.isAssigning[orderID] = false;
            }
        });
    }

    // Show assignment success banner
    private showAssignmentSuccess(orderID: number, agentName: string): void {
        this.selectedAgent[orderID] = null;

        this.assignmentBanner = {
            show: true,
            agentName: agentName,
            orderID: orderID
        };

        setTimeout(() => {
            this.assignmentBanner.show = false;
        }, 4000);
    }
}