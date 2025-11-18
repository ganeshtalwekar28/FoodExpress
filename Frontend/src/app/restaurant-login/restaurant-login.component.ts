import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../auth.service';
import { RestaurantService } from '../restaurant/restaurant.service';
import { restaurantUsers } from '../restaurantUsers';
import { RestaurantOwnerService } from '../restaurant-owner.service';

@Component({
  selector: 'app-restaurant-login',
  templateUrl: './restaurant-login.component.html',
  styleUrls: ['./restaurant-login.component.css'],
  standalone: false,
})
export class RestaurantLoginComponent implements OnInit {
  restLoginForm!: FormGroup;
  message = '';

  redirectUrl: string = '/restaurantDashboard';

  constructor(
    private fb: FormBuilder,
    private restaurantOService: RestaurantOwnerService,
    private router: Router,
    private route: ActivatedRoute,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    // Initialize restaurant login form with validators
    this.restLoginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });

    const container = document.getElementById('container') as HTMLElement;
    const registerBtn = document.getElementById('register') as HTMLButtonElement;
    const loginBtn = document.getElementById('login') as HTMLButtonElement;

    // Toggle to registration form
    if (registerBtn && container) {
      registerBtn.addEventListener('click', () => {
        container.classList.add('active');
      });
    }

    // Toggle to login form
    if (loginBtn && container) {
      loginBtn.addEventListener('click', () => {
        container.classList.remove('active');
      });
    }

    // Get redirect URL from query parameters
    this.route.queryParams.subscribe(params => {
      this.redirectUrl = '/restaurantDashboard';
    });
  }

  // Handle restaurant login success
  onLoginSuccess(): void {
    if (this.restLoginForm.invalid) return;

    const { email, password } = this.restLoginForm.value;

    // Call service to get restaurant users
    this.restaurantOService.getCustomers(email, password).subscribe((restaurant: restaurantUsers) => {
      let matchedRestUser: boolean = false;

      // Check for matching restaurant user
      if(restaurant.email === email && restaurant.password === password){
        matchedRestUser = true;
        if(restaurant.id)
        localStorage.setItem('restaurantId', restaurant.id?.toString())
        localStorage.setItem('restaurantName', restaurant.name?.toString());
      }

      // Handle login result
      if (matchedRestUser) {
        this.message = 'Login successful!';
        this.router.navigate([this.redirectUrl]);
      } else {
        this.message = 'Invalid email or password.';
        this.restLoginForm.get('email')?.setErrors({ invalid: true });
        this.restLoginForm.get('password')?.setErrors({ invalid: true });
      }
    });
  }
}