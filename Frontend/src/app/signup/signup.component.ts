import { Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../user.service';
import { User } from '../user';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css'],
  standalone: false
  
})
export class SignupComponent implements OnInit {

  // User signup form
  signupForm!: FormGroup;
  submitted = false;
  message = '';
  termsAccepted = false;
  snackbarMessage = '';
  showSnackbar = false;

  constructor(private fb: FormBuilder, private userService: UserService, private route: Router) { }

  ngOnInit(): void {

    // Initialize signup form with validators
    this.signupForm = this.fb.group({
      name: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]],
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

    // Prepare new user data
    const newUser: User = {
      ...this.signupForm.value,
      phone: this.signupForm.value.phone.toString()
    };

    // Call service to sign up new user
    this.userService.signUp(newUser).subscribe({
      next: () => {
        
        this.snackbarMessage = 'User Registered Successfully!';
        this.showSnackbar = true;
        setTimeout(() => this.showSnackbar = false, 5000);
        this.signupForm.reset();
        this.termsAccepted = false;
        this.submitted = false;
      },
      error: () => {
        
        this.snackbarMessage = 'User Registration Failed!';
        this.showSnackbar = true;
        setTimeout(() => this.showSnackbar = false, 5000);
        this.message = 'Signup failed. Please try again!';
      }
    });

    setTimeout(() => this.route.navigate(['/login']), 3000);
  }

}