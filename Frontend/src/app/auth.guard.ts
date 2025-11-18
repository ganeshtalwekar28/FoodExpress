import { Injectable } from "@angular/core";
import { CanActivate, Router } from "@angular/router";
import { AuthService } from "./auth.service";

@Injectable({ providedIn: 'root' })
export class authGuard implements CanActivate {
  constructor(private auth: AuthService, private router: Router) {}

  canActivate(): boolean {

    // Check if user is authenticated
    if (this.auth.isAuthenticated()) {
      return true;
    } else {
      this.router.navigate(['/login']);
      return false;
    }
    
    // Check if restaurant user is authenticated
    if (this.auth.restisAuthenticated()) {
      return true;
    } else {
      this.router.navigate(['/restaurantLogin']);
      return false;
    }
  }
}
