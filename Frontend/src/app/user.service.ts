import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators'; 
import { User } from './user';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiUrl = 'http://localhost:8082';
  private loginUrl = 'http://localhost:8082/auth/login';

  constructor(private http: HttpClient) { }

  // User registration
  signUp(user: User): Observable<string> {
    return this.http.post(`${this.apiUrl}/auth/register`, user, {
      responseType: 'text' as const
    });
  }

  // Fetch all customers
  getCustomers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/getCustomerData`);
  }

  // User login
  login(email: string, password: string): Observable<any> {
    return this.http.post<any>(this.loginUrl, { email, password }).pipe(
      tap((response: any) => {
        if (response.token) {
          localStorage.setItem('jwtToken', response.token);
        }
      })
    );
  }

  // Update customer details
  updateCustomer(user: User): Observable<string> {
    const token = this.getToken();
    const headers = {
      Authorization: `Bearer ${token}`
    };

    // Make PUT request to update user details
    return this.http.put(`${this.apiUrl}/auth/update`, user, {
      headers,
      responseType: 'text' as const
    });
  }

  // Retrieve JWT token from local storage
  getToken(): string | null {
    return localStorage.getItem('jwtToken');
  }
}
