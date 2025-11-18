import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MenuItem } from '../restaurant-dashboard/restaurantdb';
import { RestaurantOwnerService } from '../restaurant-owner.service';
import { HttpErrorResponse } from '@angular/common/http';
import { RestaurantService } from '../restaurant/restaurant.service';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-restaurant-dashboard',
  standalone: false,
  templateUrl: './restaurant-dashboard.component.html',
  styleUrls: ['./restaurant-dashboard.component.css'],
})
export class RestaurantDashboardComponent implements OnInit {

  // List of menu items
  menuItems: MenuItem[] = [];
  restaurantName: string = '';
  editingItem: MenuItem | null = null;
  showAddForm = false;
  itemForm: FormGroup;
  restaurantId!: number;
  snackbarMessage = '';
  showSnackbar = false;

  constructor(
    private fb: FormBuilder,
    private restaurantOwnerService: RestaurantOwnerService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {
    this.itemForm = this.fb.group({
      Itemid: [0],
      name: ['', Validators.required],
      price: [, [Validators.required, Validators.min(0)]],
      image_url: [''],
    });
  }

  // Initialize component
  ngOnInit(): void {
    this.restaurantId = Number(localStorage.getItem('restaurantId'));
    this.restaurantName = String(localStorage.getItem('restaurantName'));

    if (isNaN(this.restaurantId) || this.restaurantId === 0) {
      console.error('Invalid restaurantId found in localStorage. Redirecting to login.');
      this.router.navigate(['/restaurantLogin']);
      return; 
    }
    this.loadMenuItems();
  }

  // Load menu items for the restaurant
  loadMenuItems(): void {
    this.restaurantOwnerService
      .getMenuItemsByRestaurantId(this.restaurantId).subscribe({
        next: (items: MenuItem[]) => (this.menuItems = items),
        error: (err: HttpErrorResponse) =>
          console.error('Error fetching menu items:', err.message),
      });
  }

  // Edit menu item
  edit(item: MenuItem): void {
    this.editingItem = item;
    this.itemForm.patchValue(item);
  }

  // Save edited menu item
  saveEdit(): void {
    if (!this.editingItem) return;
    let id = this.editingItem.id;

    const updatedItem = this.itemForm.value as MenuItem;

    // Call service to update menu item
    this.restaurantOwnerService.updateMenuItem(id, updatedItem).subscribe({
      next: () => {
        const itemWithId = { ...updatedItem, id: id, };
        const index = this.menuItems.findIndex((i) => i.id === id);
        if (index !== -1) {
          this.menuItems[index] = itemWithId;
        }

        this.editingItem = null;
        
        this.snackbarMessage = '✅ Item updated successfully!';
        this.showSnackbar = true;
        setTimeout(() => this.showSnackbar = false, 3000);
      },
    });
  }

  // Cancel editing menu item
  deleteItem(itemId: number): void {
    this.restaurantOwnerService.deleteMenuItem(itemId).subscribe({
      next: () => {
        this.menuItems = this.menuItems.filter((item) => item.id !== itemId);
        
        this.snackbarMessage = '🗑️ Item deleted successfully!';
        this.showSnackbar = true;
        setTimeout(() => this.showSnackbar = false, 3000);
      },
      error: (err: HttpErrorResponse) =>
        alert(`❌ Delete failed: ${err.message}`),
    });
  }

  // Add new menu item
  addItem(): void {
    if (this.itemForm.invalid) return;

    const newItem: MenuItem = { ...this.itemForm.value };

    // Call service to create new menu item
    this.restaurantOwnerService
      .createMenuItem(this.restaurantId, newItem)
      .subscribe({
        next: (createdItem: MenuItem) => {
          this.menuItems.push(createdItem);
          this.itemForm.reset();
          this.showAddForm = false;
          
          this.snackbarMessage = '✅ Item added successfully!';
          this.showSnackbar = true;
          setTimeout(() => this.showSnackbar = false, 3000);
        },
        error: (err) => {
          alert(`❌ Failed to add item:${err}`);
          this.snackbarMessage = '❌ Failed to add item:${err}!';
          this.showSnackbar = true;
          setTimeout(() => this.showSnackbar = false, 3000);
          console.error(JSON.stringify(err));
        },
      });
  }
}