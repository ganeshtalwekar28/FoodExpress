import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CartService } from '../cart.service';
import { AuthService } from '../auth.service';
import { UserService } from '../user.service';
import { MatSnackBar } from '@angular/material/snack-bar';
 
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  standalone: false,
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  message = '';
  snackbarMessage = '';
  showSnackbar = false;
  showPassword: boolean = false;
 
  redirectUrl: string = '/home';
 
  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private router: Router,
    private route: ActivatedRoute,
    private authService: AuthService,
    private cartService: CartService,
    private snackBar: MatSnackBar
  ) { }
 
  ngOnInit(): void {
    // Initialize login form with validators
    this.loginForm = this.fb.group({
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
      this.redirectUrl = params['redirect'] || '/home';
    });
 
    if (this.loginForm.invalid) return;
 
    const { email, password } = this.loginForm.value;
 
  }
 
  // Handle login form submission
  loginFormValidation(): void {
    if (this.loginForm.invalid) return;
 
    const email = this.loginForm.get('email')?.value;
    const password = this.loginForm.get('password')?.value;
 
    // Call login API
    this.userService.login(email, password).subscribe({
      next: (response: any) => {
        const token = response.token;
        const user = response.user;
 
        if (token && user) {
          this.authService.setToken(token);
          this.authService.setCurrentUser(user);
          this.cartService.loadCart();
         
          this.snackbarMessage = 'Welcome back, ' + user.name + '!';
          this.showSnackbar = true;
          setTimeout(() => this.showSnackbar = false, 3000);
          setTimeout(()=> this.router.navigate(['/home']), 2000);
        } else {
          this.snackbarMessage = 'Login failed: Invalid response from server';
          this.showSnackbar = true;
          setTimeout(() => this.showSnackbar = false, 3000);
          console.log('Login failed: Invalid response from server');
        }
      },
      error: () => {
        this.snackbarMessage = 'Login failed: Please check your email and password';
        this.showSnackbar = true;
        setTimeout(() => this.showSnackbar = false, 3000);
      },
     
      complete: () => console.log('Login operation is complete')
    });
  }
 
  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }
}