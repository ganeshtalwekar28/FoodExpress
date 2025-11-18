import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { CartService } from '../cart.service';
import { UserCart, Cartitem } from '../models/cart.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-cart',
  standalone: false,
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit, OnDestroy {
  [x: string]: any;
  cart: UserCart | null = null;
  private sub = new Subscription();

  constructor(private cartService: CartService, private router: Router) { }

  ngOnInit(): void {
    this.sub = this.cartService.cart$.subscribe(c => this.cart = c);
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  // Increase item quantity
  increase(cartItemId: number): void {
    this.cartService.increaseQuantity(cartItemId).subscribe({
      next: () => { },
      error: err => console.error('Increase failed', err)
    });
  }

  // Decrease item quantity
  decrease(cartItemId: number): void {
    this.cartService.decreaseQuantity(cartItemId).subscribe({
      next: () => { },
      error: err => console.error('Decrease failed', err)
    });
  }

  // Remove item from cart
  remove(item: Cartitem): void {
    this.cartService.removeItem(item).subscribe({
      next: () => { },
      error: err => console.error('Remove failed', err)
    });
  }

  // Clear the entire cart
  clear(): void {
    this.cartService.clearCart().subscribe({
      next: () => { },
      error: err => console.error('Clear cart failed', err)
    });
  }

  // Calculate subtotal
  getSubtotal(): number {
    return this.cart?.items.reduce((s, i) => s + i.price * i.quantity, 0) || 0;
  }

  // Calculate delivery fee
  getDeliveryFee(): number {
    return this.getSubtotal() > 500 ? 0 : 0;
  }

  // Calculate GST (5%)
  getGST(): number {
    return Math.round(this.getSubtotal() * 0.05);
  }

  // Calculate total amount
  trackByLine(_: number, item: Cartitem): number {
    return item.cartItemId;
  }

  // Navigate to checkout page
  checkout(): void {
    this.router.navigate(['/checkout']);
  }
}
