import { ActivatedRoute, Router } from '@angular/router';
import { CartService } from '../cart.service';
import { Cartitem, UserCart } from '../models/cart.model';
import { MenuService } from '../menu.service';
import { Item } from '../restaurant-dashboard/restaurantdb';
import { MatSnackBar } from '@angular/material/snack-bar';

import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-menu',
  standalone: false,
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class MenuComponent implements OnInit {
  restaurantId!: number;
  restaurantName!: string;
  menuItems: Item[] = [];
  cart: UserCart | null = null;
  showSnackbar = false;
  snackbarMessage = '';

  constructor(
    private route: ActivatedRoute,
    private cartService: CartService,
    private MenutService: MenuService,
    private router: Router,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.cartService.cart$.subscribe(c => this.cart = c);

    this.route.paramMap.subscribe(pm => {
      const id = pm.get('id');
      if (!id) return;

      this.restaurantId = +id;
      this.loadRestaurantDetails();
      this.loadMenuItems();
    });
  }

  // Load restaurant details
  loadRestaurantDetails(): void {
    this.MenutService.getRestaurants().subscribe(restaurants => {
      const restaurant = restaurants.find(r => +r.id === this.restaurantId);
      this.restaurantName = restaurant?.name || `Restaurant #${this.restaurantId}`;
    });
  }

  // Load menu items for the restaurant
  loadMenuItems(): void {
    this.MenutService.getMenus(this.restaurantId).subscribe(menuItems => {
      this.menuItems = menuItems.map(item => ({
        ...item,
        image: item.image_url
      }));
    });
  }

  // Add item to cart
  addToCart(item: Item): void {
    const line: Cartitem = {
      cartItemId: 0,
      menuItemId: item.id,
      name: item.name,
      price: item.price,
      quantity: 1,
      originalPrice: item.originalPrice,
      image_url: item.image_url
    };

    // Prevent adding items from different restaurants
    if (this.cart && this.cart.restaurantId !== this.restaurantId) {
      this.snackBar.open(
        `Your cart already contains items from "${this.cart.restaurantName}". `,
        'Close',
        {
          duration: 7000,
        }
      );
      return;
    }

    // Add item to cart via service
    this.cartService.addItem(line, this.restaurantId, this.restaurantName).subscribe({
      next: () => {
        this.snackbarMessage = `${item.name} added to cart`;
        this.showSnackbar = true;
        setTimeout(() => this.showSnackbar = false, 1200);
      },
      error: err => {
        console.error('Failed to add item to cart', err);
        this.snackbarMessage = `Failed to add ${item.name} to cart`;
        this.showSnackbar = true;
        setTimeout(() => this.showSnackbar = false, 1500);
      }
    });
  }

  // Navigate to cart page
  proceedToCart(): void {
    this.router.navigate(['/cart']);
  }

  // Track items by their ID for performance
  trackByItemId(_: number, item: Item): number {
    return item.id;
  }
}
