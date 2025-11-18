import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Order } from './models/order.model';
import { UserCart } from './models/cart.model'; 
import { User } from './user';

@Injectable({
  providedIn: 'root'
})
export class OrderService {

  private apiBaseUrl = 'http://localhost:8082/api/auth';

  constructor(private http: HttpClient) { }

  // Get customer details by ID
  getCustomer(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiBaseUrl}/customer/${id}`);
  }

  // Get cart details by customer ID
  getCart(id: number): Observable<UserCart> {
    return this.http.get<UserCart>(`${this.apiBaseUrl}/carts/customer/${id}`); 
  }

  // Initiate payment process
  initiatePayment(amount: number, currency: string): Observable<any> {
    const url = `${this.apiBaseUrl}/payment/createOrder`;
    let params = new HttpParams()
        .set('amount', amount.toString())
        .set('currency', currency);
    return this.http.post(url, null, { params: params });
  }

  // Finalize order placement
  finalizeOrder(finalOrderData: any): Observable<any> {
    return this.http.post(`${this.apiBaseUrl}/orders/place`, finalOrderData);
  }

  // Update customer address
  updateCustomerAddress(customerId: string, updatedCustomer: User): Observable<User> {
    return this.http.put<User>(`${this.apiBaseUrl}/customer/${customerId}`, updatedCustomer);
  }
  
  // Get orders by user ID
  getOrdersByUser(userID: number): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.apiBaseUrl}/orders/user/${userID}`);
  }
}
