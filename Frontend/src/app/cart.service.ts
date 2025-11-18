import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from './auth.service';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { UserCart, Cartitem } from './models/cart.model';

@Injectable({ providedIn: 'root' })
export class CartService {
  private apiUrl = 'http://localhost:8082/api/auth/carts';
  private cartSubject = new BehaviorSubject<UserCart | null>(null);
  public cart$ = this.cartSubject.asObservable();

  constructor(private http: HttpClient, private auth: AuthService) {
    // Subscribe to authentication changes to load or clear cart
    this.auth.currentUser$.subscribe(user =>
      user ? this.loadCart() : this.cartSubject.next(null)
    );
  }

  // Get current user ID
  private get userID(): number {
    return this.auth.getUserId();
  }

  // Load cart for the current user
  public loadCart(): void {
    this.http.get<UserCart>(`${this.apiUrl}/customer/${this.userID}`)
      .pipe(
        catchError(err => {
          if (err.status === 404) {
            this.cartSubject.next(null);
            return of(null);
          }
          throw err;
        })
      )
      .subscribe(cart => {
        if (cart) this.cartSubject.next(cart);
      });
  }

  // Increase item quantity
  public increaseQuantity(cartItemId: number): Observable<UserCart | null> {
    return this.http.put<UserCart>(
      `${this.apiUrl}/customer/${this.userID}/items/${cartItemId}?quantity=1`,
      {}
    ).pipe(
      tap(cart => this.cartSubject.next(cart)),
      catchError(err => {
        
        console.error('Increase failed', err);
        return of(this.cartSubject.value);
      })
    );
  }

  // Decrease item quantity
  public decreaseQuantity(cartItemId: number): Observable<UserCart | null> {
    return this.http.put<UserCart>(
      `${this.apiUrl}/customer/${this.userID}/items/${cartItemId}?quantity=-1`,
      {}
    ).pipe(
      tap({
        next: (cart) => {
          if (cart && cart.items && cart.items.length > 0) {
            this.cartSubject.next(cart);
          } else {
            this.cartSubject.next(null);
          }
        },
        error: () => {  }
      }),
      catchError(err => {
        
        if (err && err.status === 204) {
          this.cartSubject.next(null);
          return of(null);
        }
        console.error('Decrease failed', err);
        return of(this.cartSubject.value);
      })
    );
  }

  // Add item to cart
  public addItem(item: Cartitem, restID: number, restaurantName: string): Observable<UserCart | null> {
    return this.http.post<UserCart>(
      `${this.apiUrl}/customer/${this.userID}/restaurant/${restID}/items/${item.menuItemId}?quantity=${item.quantity}`,
      {}
    ).pipe(
      tap(cart => {
        if (cart) {
          this.cartSubject.next(cart);
        } else {
          
          this.cartSubject.next(null);
        }
      }),
      catchError(err => {
        console.error('Add item failed', err);
        return of(this.cartSubject.value);
      })
    );
  }

  // Remove item from cart
  public removeItem(item: Cartitem): Observable<UserCart | null> {
    return this.http.delete<UserCart>(
      `${this.apiUrl}/customer/${this.userID}/items/${item.cartItemId}`,
      { observe: 'response' as const }
    ).pipe(
      tap(response => {
        if (response.status === 204) {
          this.cartSubject.next(null);
        } else {
          const cart = response.body as UserCart;
          if (cart && cart.items && cart.items.length > 0) {
            this.cartSubject.next(cart);
          } else {
            this.cartSubject.next(null);
          }
        }
      }),
      map(response => response.body || null),
      catchError(err => {
        if (err && err.status === 204) {
          this.cartSubject.next(null);
          return of(null);
        }
        console.error('Remove failed', err);
        return of(this.cartSubject.value);
      })
    );
  }

  // Clear the entire cart
  public clearCart(): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/customer/${this.userID}`).pipe(
      tap(() => this.cartSubject.next(null))
    );
  }
}