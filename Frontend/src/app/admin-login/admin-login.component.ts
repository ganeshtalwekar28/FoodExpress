import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../auth.service';
import { Admin } from '../models/admin.model';

@Component({
  selector: 'app-admin-login',
  standalone: false,
  templateUrl: './admin-login.component.html',
  styleUrl: './admin-login.component.css'
})

export class AdminLoginComponent implements OnInit {
  restLoginForm!: FormGroup;
  message = '';
  showPassword: boolean = false;


  redirectUrl: string = '/admin-dashboard';

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    // Initialize the reactive form
    this.restLoginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });

    const container = document.getElementById('container') as HTMLElement;
    const registerBtn = document.getElementById('register') as HTMLButtonElement;
    const loginBtn = document.getElementById('login') as HTMLButtonElement;

    // Toggle between login and register views
    if (registerBtn && container) {
      registerBtn.addEventListener('click', () => {
        container.classList.add('active');
      });
    }

    // Toggle back to login view
    if (loginBtn && container) {
      loginBtn.addEventListener('click', () => {
        container.classList.remove('active');
      });
    }

    // Get redirect URL from query parameters if available
    this.route.queryParams.subscribe(params => {
      this.redirectUrl = '/admin-dashboard';
    });
  }

  // Handle form submission for admin login
  onLoginSuccess(): void {
    if (this.restLoginForm.invalid) return;

    const { email, password } = this.restLoginForm.value;

    const Admin1: Admin = {
      aName: 'Admin',
      aEmail: 'admin@gmail.com',
      password: 'admin123'
    };

    // Validate admin credentials
    if (email === Admin1.aEmail && password === Admin1.password) {
      this.message = 'Login successful!';
      this.authService.adminLogin(Admin1);
      this.router.navigate([this.redirectUrl]);
    } else {
      this.message = 'Invalid email or password.';
      this.restLoginForm.get('email')?.setErrors({ invalid: true });
      this.restLoginForm.get('password')?.setErrors({ invalid: true });
      this.router.navigate(['/adminLogin']);
    }

  }
  
  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }
}
