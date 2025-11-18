import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { MenuItem } from './restaurant-dashboard/restaurantdb';
import { restaurantUsers } from './restaurantUsers';

@Injectable({
  providedIn: 'root'
})
export class RestaurantOwnerService {
  private apiUrl = 'http://localhost:8082';

  constructor(private http: HttpClient) { }

  // Fetch menu items by restaurant ID
  getMenuItemsByRestaurantId(restaurantId: number): Observable<MenuItem[]> {
    return this.http.get<MenuItem[]>(`${this.apiUrl}/api/auth/menu-items/getMenuItemsByRestaurantId/restaurant/${restaurantId}`).pipe(
      tap(items => {
        items.forEach(item => console.log('Fetched menu item:', item));
      })
    );
  }

  // Create new menu item
  createMenuItem(restaurantId: number, item: MenuItem): Observable<MenuItem> {
    return this.http.post<MenuItem>(`${this.apiUrl}/api/auth/menu-items/createMenuItem/restaurant/${restaurantId}`, item);
  }

  // Update existing menu item
  updateMenuItem(itemId: number, item: MenuItem): Observable<MenuItem> {
    return this.http.put<MenuItem>(`${this.apiUrl}/api/auth/menu-items/updateMenuItem/${itemId}`, item);
  }

  // Delete menu item
  deleteMenuItem(itemId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/api/auth/menu-items/deleteMenuItem/${itemId}`);
  }

  // Fetch restaurant by email and password
  getCustomers(email: String, password: String): Observable<restaurantUsers> {
    return this.http.get<restaurantUsers>(`${this.apiUrl}/api/auth/restaurants/${email}/${password}`);
  }
}
