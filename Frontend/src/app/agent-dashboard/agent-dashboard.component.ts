import { Component, OnInit } from '@angular/core';
import { Orders } from '../models/orders';
import { DeliveryAgent } from '../models/delivery-agent';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-agent-dashboard',
  standalone: false,
  templateUrl: './agent-dashboard.component.html',
  styleUrls: ['./agent-dashboard.component.css']
})
export class AgentDashboardComponent implements OnInit {

  agent: DeliveryAgent | null = null;
  currentOrder: Orders | null = null;
  deliveryConfirmed = false;
  currentOrderMessage: string | null = null;

  private backendBaseUrl1 = 'http://localhost:8082/api/auth/admin';
  private backendBaseUrl2 = 'http://localhost:8082/api/auth/orders';
  
  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private router: Router
  ) { }

  ngOnInit(): void {
    const agentId = this.route.snapshot.paramMap.get('id');
    if (!agentId) return;

    // Fetch agent details
    this.http.get<DeliveryAgent>(`${this.backendBaseUrl1}/delivery-agents/${agentId}`).subscribe(agentData => { 
      if (!agentData) return;
      this.agent = agentData;

      if (this.agent.currentOrderID) {
        
        this.http.get<any>(`${this.backendBaseUrl2}/admin/${this.agent.currentOrderID}`).subscribe(orderData => { 
          if (!orderData) {
            this.currentOrder = null;
            return;
          }

          
          this.currentOrder = this.mapOrder(orderData);
        }, () => {
          this.currentOrder = null;
        });
      } else {
        this.currentOrder = null;
      }
    }, () => {
      
      this.agent = null;
      this.currentOrder = null;
    });
  }
  
  // Normalize status strings for consistent comparison
  private normalizeStatus(s?: string): string {
    return (s || '').toString().trim().toLowerCase().replace(/_/g, ' ');
  }

  // Navigate back to agent details
  goBack(): void {
    this.router.navigate(['/agent-details']); 
  }

  // Show delivery confirmation animation
  showDeliveryAnimation(): void {
    
    this.deliveryConfirmed = true;
    
    const ANIMATION_DISPLAY_TIME = 3000;

    setTimeout(() => {
      
      this.deliveryConfirmed = false;
    }, ANIMATION_DISPLAY_TIME);
  }

  // Mark current order as delivered
  markAsDelivered(order: Orders | null): void {
    if (!order || !this.agent || !this.agent.id) return;

    const orderId = order.id ?? order.orderID;
    const agentId = this.agent.id;
    const ANIMATION_DURATION_MS = 2500; 

    const payload = {
      orderId: Number(orderId),
      agentId: agentId
    };

    // Send PUT request to mark order as delivered
    this.http.put(`${this.backendBaseUrl2}/admin/${orderId}/deliver`, payload).subscribe(() => { 
      
      this.showDeliveryAnimation();
      
      setTimeout(() => {
        
        this.http.get<DeliveryAgent>(`${this.backendBaseUrl1}/delivery-agents/${agentId}`).subscribe(updatedAgent => { 
          this.agent = updatedAgent;

          
          if (!updatedAgent.currentOrderID) {
            this.currentOrder = null;
            this.currentOrderMessage = 'No current order assigned.';
          } else {
            
            this.http.get<any>(`${this.backendBaseUrl2}/admin/${updatedAgent.currentOrderID}`).subscribe(orderData => { 
              this.currentOrder = this.mapOrder(orderData);
            });
          }

          
          setTimeout(() => {
            this.currentOrderMessage = null;
          }, 500);
        });
      }, ANIMATION_DURATION_MS);

    }, err => {
      console.error('Failed to mark delivered', err);
      try { console.error('Server error payload:', err.error); } catch (e) {  }
    });
  }

  // Map raw order data to Orders model
  private mapOrder(orderData: any): Orders {
    return {
      id: orderData.orderId ?? orderData.id ?? null,
      orderID: orderData.orderId ?? orderData.id,
      status: (orderData.orderStatus ?? orderData.status ?? 'Unknown')?.toString(),
      restaurantName: orderData.restaurantName ?? '',
      pickupAddress: orderData.restaurantAddress ?? '',
      customerName: orderData.customerName ?? '',
      dropAddress: orderData.customerAddress ?? orderData.dropAddress ?? '',
      items: (orderData.items || []).map((it: any) => ({
        itemID: it.id ?? 0,
        name: it.name,
        price: it.unitPrice ?? 0,
        quantity: it.quantity ?? 1
      })),
      totalItems: (orderData.items || []).length,
      totalAmount: orderData.totalAmount ?? 0,
      orderDate: orderData.orderDate ?? '',
      agentName: orderData.agentName ?? ''
    } as Orders;
  }
}