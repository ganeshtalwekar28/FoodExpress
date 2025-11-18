import { Component, OnInit } from '@angular/core';
import { RestaurantService } from '../restaurant/restaurant.service';
import { ActivatedRoute, Router } from '@angular/router';
import { OrderService } from '../orders.service';
import { AuthService } from '../auth.service';
import { Order } from '../models/order.model';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-home',
  standalone: false,
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent implements OnInit {
  latestOrder: Order | null = null;
  showSnackbar = false;
  snackbarMessage = '';

  restaurants: any[] = [];
  id!: number;

  constructor(
    private route: ActivatedRoute,
    private restaurantService: RestaurantService,
    private router: Router,
    private orderService: OrderService,
    private auth: AuthService,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    
    // Fetch list of restaurants
    this.restaurantService.getRestaurants().subscribe((data) => {
      this.restaurants = data;
    });

    // Check for newly placed order to show snackbar
    const userID = this.auth.getUserId();
    
    const orderPlacedFlag = localStorage.getItem('newOrderPlaced');

    // Show snackbar if a new order was placed
    if (orderPlacedFlag === 'true') {
      this.orderService.getOrdersByUser(userID).subscribe((orders) => {
        if (orders.length > 0) {
          
          this.latestOrder = orders.sort(
            (a, b) =>
              new Date(b.orderDate).getTime() - new Date(a.orderDate).getTime()
          )[0];

          if (this.latestOrder) {
            
            // Show success snackbar
            this.snackBar.open(this.snackbarMessage=`Your order is placed successfully!`, 'Close', {
              duration: 6000,
              panelClass: ['success-snackbar'],
              verticalPosition: 'bottom',
              horizontalPosition: 'center',
            });
            localStorage.removeItem('newOrderPlaced');
            setTimeout(() => (this.showSnackbar = false), 6000);
          }
        }
      });
    }
  }
}
