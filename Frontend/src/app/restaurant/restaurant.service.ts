import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map } from 'rxjs';
import { Observable } from 'rxjs';
import { restaurantUsers } from '../restaurantUsers';
import { MenuItem } from '../restaurant-dashboard/restaurantdb';



@Injectable({
  providedIn: 'root'
})
export class RestaurantService {

  private apiurl = 'http://localhost:8082';

  constructor(private http: HttpClient) { }

  // Fetch all restaurants
  getRestaurants(): Observable<restaurantUsers[]> {
    return this.http.get<restaurantUsers[]>(`${this.apiurl}/api/auth/restaurants/getAllRestaurants`);
  }

  // Fetch menu items by restaurant ID
  getMenuItemsByRestaurantId(restaurantId: number): Observable<MenuItem[]> {
    return this.http.get<MenuItem[]>(`${this.apiurl}/api/auth/menu-items/getMenuItemsByRestaurantId/restaurant/${restaurantId}`);
  }

  // Fetch restaurant by email and password
  getCustomers(email: String, password: String): Observable<restaurantUsers> {
    return this.http.get<restaurantUsers>(`${this.apiurl}/api/auth/restaurants/${email}/${password}`);
  }

  private restUrl = 'http://localhost:8082/api/auth/restaurants';

  // Register new restaurant
  signUp(restaurant: restaurantUsers): Observable<restaurantUsers> {
    return this.http.post<restaurantUsers>(`${this.restUrl}/createRestaurant`, restaurant);
  }

  // Update restaurant details
  updateCustomer(id: number, restaurants: restaurantUsers): Observable<restaurantUsers> {
    return this.http.put<restaurantUsers>(`${this.restUrl}/${id}`, restaurants);
  }

  // Fetch all menu items
  getAllMenuItems(): Observable<any[]> {
    return this.http.get<any[]>('/api/auth/menu-items');
  }

  // Search menu items by name
  searchMenuItems(query: string): Observable<any[]> {
    return this.getAllMenuItems().pipe(
      map(items => items.filter(item => item.name.toLowerCase().includes(query.toLowerCase())))
    );
  }
}