import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../auth.service';
import { CartService } from '../cart.service';
import { OrderService } from '../orders.service';
 
declare var Razorpay: any;
 
@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  standalone: false,
  styleUrls: ['./checkout.component.css']
})
export class CheckoutComponent implements OnInit {

  // variables to hold
  cartDetails: any;
  customerData: any;
  currentUserId: number | null = null;
 
  totalAmountWithGST: number = 0;
  deliveryFee: number = 0; 
  gstPercentage: number = 0.05; 
  
  billingAddressForm!: FormGroup;
  
  // Razorpay Key ID kept readonly because it should not be changed
  readonly razorpayKeyId: string = 'rzp_test_Ra3nmIP3wYElZa';
 
  // Inject necessary services
  constructor(
    private orderService: OrderService,
    private authService: AuthService,
    private router: Router,
    private cartService: CartService,
    private fb: FormBuilder
  ) {}
  
  // Initialize component
  ngOnInit(): void {
    
    // Initialize billing address form with validators
    this.billingAddressForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phoneNumber: ['', [Validators.required, Validators.pattern(/^\d{10}$/)]],
      address: ['', Validators.required],
      state: ['', Validators.required],
      city: ['', Validators.required],
      zip: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]],
    });
    
    // Get current user ID
    this.currentUserId = this.authService.getUserId();
    
    // Fetch customer and cart data if user is logged in
    if (this.currentUserId) {
      this.fetchCustomerData(this.currentUserId);
      this.fetchCartData(this.currentUserId);
    } else {
      console.error("No user is logged in. Please log in to view checkout.");
      this.router.navigate(['/login']);
    }
  }
  
  // Fetch customer data to prefill billing form
  fetchCustomerData(userId: number): void {
    this.orderService.getCustomer(userId).subscribe({
      next: customer => {
        if (customer) {
          this.customerData = customer;
          this.billingAddressForm.patchValue({
            firstName: customer.name ? customer.name.split(' ')[0] : '',
            lastName: customer.name ? customer.name.split(' ').slice(1).join(' ') : '',
            email: customer.email || '',
            phoneNumber: customer.phone || ''
          });
        } else {
          console.warn('Customer data not found. Cannot prefill form.');
        }
      },
      error: err => {
        console.error('Error fetching customer data:', err);
      }
    });
  }
 
  // Fetch cart details for the user 
  fetchCartData(userId: number): void {
    this.orderService.getCart(userId).subscribe({
      next: cart => {
        if (cart) {
          this.cartDetails = cart;
          this.calculateFinalTotal();
        }
      },
      error: err => {
        console.error('Error fetching cart details:', err);
      }
    });
  }
 
  // Calculate final total including GST and delivery fee
  calculateFinalTotal(): void {
    if (this.cartDetails && this.cartDetails.totalAmount) {
      const subtotal = this.cartDetails.totalAmount;
      
      console.log(`Calculating total. Subtotal from cart is: ${subtotal}`);
     
      const gstAmount = subtotal * this.gstPercentage + 0.5;
      this.totalAmountWithGST = subtotal + gstAmount + this.deliveryFee;
    }
  }
 
  // Finalize order after successful payment
  finalizeOrder(razorpayResponse: any, razorpayOrderId: string): void {
    
    const addressData = this.billingAddressForm.value;
    
    // Prepare final order data to send to backend
    const finalOrderData = {
      customerId: this.currentUserId,
      totalAmount: this.totalAmountWithGST, 
      deliveryAddress: `${addressData.address}, ${addressData.city}, ${addressData.state} - ${addressData.zip}`,
      
      razorpayOrderId: razorpayResponse.razorpay_order_id,
      razorpayPaymentId: razorpayResponse.razorpay_payment_id,
      razorpaySignature: razorpayResponse.razorpay_signature
    };
    
    // Send final order data to backend to complete the order
    this.orderService.finalizeOrder(finalOrderData).subscribe({
      next: (order) => {
        console.log('Order placed successfully:', order);
        
        localStorage.setItem('newOrderPlaced', 'true');
        
        this.cartService.clearCart().subscribe(() => {
          this.router.navigate(['/']);
        });
      },
      error: (err) => {
        console.error('Failed to finalize order:', err);
        console.log('There was an error placing your order. Please contact support.');
      }
    });
  }
 
  // Handle place order button click
  onPlaceOrder(): void {
    
    if (this.billingAddressForm.invalid) {
      console.log('Please fill out all billing and delivery address details correctly.');
      this.billingAddressForm.markAllAsTouched();
      return;
    }
    
    const amountInPaise = Math.round(this.totalAmountWithGST * 100);
    
    // Initiate payment with Razorpay
    this.orderService.initiatePayment(amountInPaise, 'INR').subscribe({
      next: (razorpayOrder) => {
        console.log('Razorpay Order from Backend (before options):', razorpayOrder);
        
        const addressData = this.billingAddressForm.value;
        const finalName = `${addressData.firstName} ${addressData.lastName}`;
        
        // Configure Razorpay payment options
        const options = {
          key: this.razorpayKeyId,
          amount: razorpayOrder.amountInPaise,
          currency: razorpayOrder.currency,
          name: 'FoodExpress',
          description: 'Payment for your food order',
          image : 'http://localhost:4200/logo.jpg',
          order_id: razorpayOrder.orderId,
          prefill: {
            name: finalName,
            email: addressData.email,
            contact: addressData.phoneNumber
          },
          notes: {
            address: `${addressData.address}, ${addressData.city}, ${addressData.state} - ${addressData.zip}`
          },
          theme: {
            color: '#f97316'
          },
          handler: (response: any) => {
            console.log('Razorpay Response from Handler:', response);
            
            this.finalizeOrder(response, razorpayOrder.razorpayOrderId);
          },
          modal: {
            ondismiss: () => {
              alert('Payment was cancelled by the user. Please try again.');
            }
          }
        };
 
        // Open Razorpay payment modal
        const rzp = new Razorpay(options);
        rzp.open();
      },
      error: (err) => {
        console.error('Failed to initiate payment with Razorpay:', err);
      }
    });
  }
}
 