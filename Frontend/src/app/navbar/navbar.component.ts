import { Component, Input, OnInit, OnDestroy, HostListener, ElementRef } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';
import { UserCart } from '../models/cart.model';
import { CartService } from '../cart.service';
import { MenuService } from '../menu.service';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
  standalone: false
})
export class NavbarComponent implements OnInit, OnDestroy {
  @Input() cartItemsCount: number = 0;
  isMobileMenuOpen = false;
  searchQuery: string = '';
  searchResults: any[] = [];
  showDropdown = false;
  searchSubject = new Subject<string>();

  showLocationDropdown: boolean = false;
  currentCity: string = 'Coimbatore'; 
  majorCities: string[] = ['Coimbatore', 'Mumbai', 'Delhi', 'Pune', 'Nagpur', 'Bangalore', 'Chennai', 'Hyderabad'];
  
  // Define navigation links
  navLinks = [
    { href: '/home', label: 'Home' },
    { href: '/restaurant', label: 'Restaurants' },
    { href: '/aboutus', label: 'About Us' },
    { href: '/contactus', label: 'Contact' }
  ];

  // User cart
  cart: UserCart | null = null;

  constructor(
    private router: Router,
    private authService: AuthService,
    private cartService: CartService,
    private menuService: MenuService,
    private elementRef: ElementRef 
  ) { }

  ngOnInit(): void {

    // Subscribe to cart updates
    this.cartService.cart$.subscribe(c => this.cart = c);

    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(query => {
      if (query.trim()) {
        this.menuService.searchMenuItems(query).subscribe(results => {
          this.searchResults = results;
          this.showDropdown = true;
        });
      } else {
        this.searchResults = [];
        this.showDropdown = false;
      }
    });
  }

  // Close dropdown when clicking outside
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.showLocationDropdown = false;
    }
  }

  // Cleanup on destroy
  ngOnDestroy(): void {}
  
  // Toggle location dropdown
  toggleLocationDropdown() {
    this.showLocationDropdown = !this.showLocationDropdown;
    if (this.showLocationDropdown) {
      this.showDropdown = false;
    }
  }

  // Select a city
  selectCity(city: string) {
    this.currentCity = city;
    this.showLocationDropdown = false;
  }

  // Get cart item count
  get cartItemCount(): number {
    return this.cart?.itemCount ?? 0;
  }

  // Handle search input changes
  onSearchChange(query: string) {
    this.showLocationDropdown = false;
    this.searchSubject.next(query);
  }

  // Handle selection of a search result
  onSelectItem(item: any) {
    this.showDropdown = false;
    this.router.navigate(['/restaurant', item.restaurantId]);
  }

  // Handle cart icon click
  onCartClick() {
    this.navigateWithAuth('/cart');
  }

  // Handle profile icon click
  onProfileClick() {
    this.navigateWithAuth('/profile');
  }

  // Navigate with authentication check
  navigateWithAuth(targetRoute: string) {
    if (this.authService.isAuthenticated()) {
      this.router.navigate([targetRoute]);
    } else {
      this.router.navigate(['/login'], { queryParams: { redirectTo: targetRoute } });
    }
  }
}