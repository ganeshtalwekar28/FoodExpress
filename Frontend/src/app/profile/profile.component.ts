import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';
import { Order } from '../models/order.model';
import { User } from '../user';
import { OrderService } from '../orders.service';
import { UserService } from '../user.service';

@Component({
  selector: 'app-profile',
  standalone: false,
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {

  // Current logged-in user details
  currentUser: any;
  activeTab: string = 'profile';
  orders: Order[] = [];

  // Frequently Asked Questions
  faqs = [
    {
      question: 'How do I track my order?',
      answer: 'Go to the "Orders" tab to see real-time updates on your order status.',
      icon: 'bi bi-truck'
    },
    {
      question: 'What payment methods are accepted?',
      answer: 'UPI, cards, net banking, wallets, and cash on delivery (select restaurants).',
      icon: 'bi bi-credit-card'
    },
    {
      question: 'How do I update my delivery address?',
      answer: 'Use the "Edit Profile" tab to update your saved address.',
      icon: 'bi bi-geo-alt'
    },
    {
      question: 'Is contactless delivery available?',
      answer: 'Yes! Select "Contactless Delivery" at checkout.',
      icon: 'bi bi-shield-check'
    }
  ];

  // Inject necessary services
  constructor(
    private authService: AuthService, 
    private router: Router, 
    private OrderService: OrderService, 
    private UserService: UserService
  ) { }

  // Initialize component
  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    const userID = this.authService.getUserId();

    // Fetch user orders
    this.OrderService.getOrdersByUser(userID).subscribe(data => {
      console.log(data);
      this.orders = data;
    })
  }

  // Handle user
  logout() {
    this.authService.logout();
    this.router.navigate(['/login'], { queryParams: { redirectTo: '/' } });
  }

  // Handle profile update
  onUpdateCustomer(): void {
    
    // Call the UserService to update customer details
    this.UserService.updateCustomer(this.currentUser).subscribe({
      next: (response) => {
        alert(response); 
      },
      error: (err) => {
        console.error('Update failed:', err);
        alert('Update failed. Please try again.');
      }
    });
  }

}