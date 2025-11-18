import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { RestaurantService } from './restaurant.service';

import { restaurantUsers } from '../restaurantUsers';

@Component({
  selector: 'app-restaurant',
  standalone: false,
  templateUrl: './restaurant.component.html',
  styleUrl: './restaurant.component.css'
})
export class RestaurantComponent implements OnInit {
  // List of restaurants
  restaurants: any[] = [];
  id!: number;
  selectedRestaurantName!: string;
  selectedMenuItems: any;

  // Inject RestaurantService and Router
  constructor(
    private restaurantService: RestaurantService,
    private router: Router
  ) { }

  // Initialize component
  ngOnInit(): void {
    this.restaurantService.getRestaurants().subscribe(data => {
      this.restaurants = data;  
    });
  }

  // Handle restaurant click to load menu items
  onRestaurantClick(restaurant: restaurantUsers): void {
    this.selectedRestaurantName = restaurant.name; 
    this.restaurantService.getMenuItemsByRestaurantId(restaurant.id).subscribe(menu => { 
      this.selectedMenuItems = menu;   
    });
  }
}

