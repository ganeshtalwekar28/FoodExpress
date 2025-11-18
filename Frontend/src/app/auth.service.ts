import { restaurantUsers } from './restaurantUsers';
import { Admin } from './models/admin.model';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { User } from './user';

@Injectable({ providedIn: 'root' })
export class AuthService {

  // Set the current user and store in localStorage
  setCurrentUser(user: User): void {
    this.currentUserSubject.next(user);
    localStorage.setItem('currentUser', JSON.stringify(user));
  }

  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    // Initialize current user from localStorage
    const saved = localStorage.getItem('currentUser');
    if (saved) {
      try {
        this.currentUserSubject.next(JSON.parse(saved));
      } catch { }
    }
  }

  // User login
  login(user: User): void {
    this.currentUserSubject.next(user);
    localStorage.setItem('currentUser', JSON.stringify(user));
  }

  // User logout
  logout(): void {
    this.currentUserSubject.next(null);
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('currentUser');
  }

  // Get current authenticated user
  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  // Get current user ID
  getUserId(): number {
    const u = this.currentUserSubject.value;

    // Ensure user is authenticated and has an ID
    if (!u || u.id === undefined) {
      throw new Error('User not authenticated or ID missing');
    }

    return u.id;
  }

  // Check if user is authenticated
  isAuthenticated(): boolean {
    return this.currentUserSubject.value !== null;
  }

  // Restaurant user authentication
  private restisLoggedIn = false;
  private currentRestUser: restaurantUsers | null = null;

  // Restaurant user login
  restLogin(restaurant: restaurantUsers) {
    this.restisLoggedIn = true;
    this.currentRestUser = restaurant;
  }

  // Restaurant user logout
  restLogout() {
    this.restisLoggedIn = false;
    this.currentRestUser = null;
  }

  // Get current authenticated restaurant user
  getCurrentRestUser(): restaurantUsers | null {
    return this.currentRestUser;
  }

  // Check if restaurant user is authenticated
  restisAuthenticated(): boolean {
    return this.restisLoggedIn;
  }

  // Store JWT token in localStorage
  setToken(token: string): void {
    localStorage.setItem('jwtToken', token);
  }

  // Retrieve JWT token from localStorage
  getToken(): string | null {
    return localStorage.getItem('jwtToken');
  }

  // Admin user authentication
  private adminisLoggedIn = false;
  private currentAdminUser: Admin | null = null;

  // Admin user login
  adminLogin(admin: Admin) {
    this.adminisLoggedIn = true;
    this.currentAdminUser = admin;
  }

  // Admin user logout
  adminLogout() {
    this.adminisLoggedIn = false;
    this.currentAdminUser = null;
  }

  // Get current authenticated admin user
  getAdminByEmail(email: string): Observable<Admin[]> {
    return this.http.get<Admin[]>(`http://localhost:3000/admin?aEmail=${email}`);
  }

}