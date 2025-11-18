import { Injectable } from '@angular/core';
import { UserService } from './user.service';
import { HttpEvent, HttpRequest } from '@angular/common/module.d-CnjH8Dlt';
import { HttpHandler } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthInterceptorService {

  constructor(private user: UserService) { }

  // Intercept HTTP requests to add Authorization header
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const isPublicEndpoint = req.url.includes('/auth/register') || req.url.includes('/auth/login');

    // Bypass adding token for public endpoints
    if (isPublicEndpoint) {
      return next.handle(req);
    }

    // Retrieve token from UserService
    const token = this.user.getToken();

    // If token exists, clone request and add Authorization header
    if (token) {
      const clone = req.clone({
        headers: req.headers.set('Authorization', 'Bearer ' + token)
      });
      console.log('To be sent for the server : ' + JSON.stringify(clone));
      return next.handle(clone);
    }

    // If no token, proceed with original request
    return next.handle(req);
  }

}



