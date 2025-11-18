import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';
import { restaurantUsers } from '../restaurantUsers';

@Component({
  selector: 'app-restaurant-navbar',
  standalone: false,
  templateUrl: './restaurant-navbar.component.html',
  styleUrl: './restaurant-navbar.component.css'
})
export class RestaurantNavbarComponent {
  r: any;
  currRestUser: any;
  
  constructor(private router: Router, private authService: AuthService) { }

  
  ngOnInit(): void {
    this.r = this.authService.getCurrentRestUser();
  }
  
  // Handle restaurant logout
  restaurantLogout() {
    this.authService.restLogout();
    localStorage.removeItem('restaurantId');
    localStorage.removeItem('restaurantName');
    this.router.navigate(['/restaurantLogin']);
  }
}
