import { Router, RouterModule } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule, NgIf } from '@angular/common';
import { RestaurantService } from '../restaurant/restaurant.service';
import { restaurantUsers } from '../restaurantUsers';

@Component({
  selector: 'app-restaurant-signup',
  templateUrl: './restaurant-signup.component.html',
  styleUrls: ['./restaurant-signup.component.css'],
  standalone: false

})
export class RestaurantSignupComponent implements OnInit {
  // Restaurant signup form
  signupForm!: FormGroup;
  submitted = false;
  message = '';
  termsAccepted = false;
  snackbarMessage = '';
  showSnackbar = false;

  constructor(private fb: FormBuilder, private restaurantsService: RestaurantService, private route: Router) { }

  ngOnInit(): void {
    // Initialize signup form with validators
    this.signupForm = this.fb.group({
      name: ['', [Validators.required]],
      owner_name: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]],
      address: ['', [Validators.required]],
      cuisine_type: ['', [Validators.required]],
      image_url: ['', [Validators.required]],
      termsAccepted: [false, [Validators.requiredTrue]]
    });

  }

  // Getter for easy access to form fields
  get f() {
    return this.signupForm.controls;
  }

  // Handle form submission
  onSubmit(): void {
    this.submitted = true;

    if (this.signupForm.invalid) return;

    const newUser: restaurantUsers = this.signupForm.value;

    // Call service to sign up new restaurant user
    this.restaurantsService.signUp(newUser).subscribe({
      next: () => {
        this.snackbarMessage = 'Restaurant signed up successfully!';
        this.showSnackbar = true;
        setTimeout(() => this.showSnackbar = false, 5000);
        this.signupForm.reset();
        this.termsAccepted = false;
        this.submitted = false;
      },
      error: () => {
        this.snackbarMessage = 'Signup failed. Please try again!';
        this.showSnackbar = true;
        setTimeout(() => this.showSnackbar = false, 5000);
        this.message = 'Signup failed. Please try again!';
      }
    });

    setTimeout(() => this.route.navigate(['/restaurantLogin']), 3000);
  }
}
