import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { MenuItem } from './restaurant-dashboard/restaurantdb';
import { SearchMenuItem } from './models/searchmenuitem';

@Injectable({
  providedIn: 'root'
})
export class MenuService {
  private apiurl = "http://localhost:8082"
  constructor(private http: HttpClient) { }

  // Fetch all restaurants
  getRestaurants(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiurl}/api/auth/restaurants/getAllRestaurants`);
  }

  // Fetch menu items by restaurant ID
  getMenus(restaurantId: number): Observable<MenuItem[]> {
    return this.http.get<MenuItem[]>(`${this.apiurl}/api/auth/menu-items/getMenuItemsByRestaurantId/restaurant/${restaurantId}`);

  }

  // Fetch all menu items
  getAllMenuItems(): Observable<MenuItem[]> {
    return this.http.get<any[]>(`${this.apiurl}/menus`).pipe(
      map((menus: any[]) => {
        return menus.flatMap(menu =>
          (menu.items || []).map((item: any) => ({
            ...item,
            restaurantId: menu.restaurantId
          }))
        );
      }),
      catchError(() => of([]))
    );
  }

  // Search menu items by name
  searchMenuItems(query: string): Observable<SearchMenuItem[]> {
    // Return empty array if query is empty
    if (!query.trim()) {
      return of([]); 
    }
    
    // Construct query parameters
    let params = new HttpParams().set('query', query);
    
    // Make HTTP GET request to search endpoint
    return this.http.get<SearchMenuItem[]>(`${this.apiurl}/api/auth/menu-items/search`, { params: params })
      .pipe(
        catchError(error => {
          console.error('Menu Search Error:', error);
          
          return of([]); 
        })
      );
  }
}