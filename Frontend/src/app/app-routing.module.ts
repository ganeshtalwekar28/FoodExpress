import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CheckoutComponent } from './checkout/checkout.component';
import { MenuComponent } from './menu/menu.component';
import { CartComponent } from './cart/cart.component';
import { HomeComponent } from './home/home.component';
import { LoginComponent } from './login/login.component';
import { ProfileComponent } from './profile/profile.component';
import { RestaurantComponent } from './restaurant/restaurant.component';
import { SignupComponent } from './signup/signup.component';
import { authGuard } from './auth.guard';
import { RestaurantLoginComponent } from './restaurant-login/restaurant-login.component';
import { RestaurantSignupComponent } from './restaurant-signup/restaurant-signup.component';
import { RestaurantDashboardComponent } from './restaurant-dashboard/restaurant-dashboard.component';
import { AdminLoginComponent } from './admin-login/admin-login.component';
import { NavbarComponent } from './navbar/navbar.component';
import { FooterComponent } from './footer/footer.component';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';
import { AdminHeaderComponent } from './admin-header/admin-header.component';
import { AdminFooterComponent } from './admin-footer/admin-footer.component';
import { AdminOrdersComponent } from './admin-orders/admin-orders.component';
import { AgentDashboardComponent } from './agent-dashboard/agent-dashboard.component';
import { AgentDetailsComponent } from './agent-details/agent-details.component';
import { AboutUsComponent } from './about-us/about-us.component';
import { HelpPageComponent } from './help-page/help-page.component';

const routes: Routes = [

  // Default Customer routes
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'home', component: HomeComponent },
  { path: 'restaurant', component: RestaurantComponent },
  { path: 'restaurant/:id', component: MenuComponent },
  { path: 'restaurant/:id', component: MenuComponent },
  { path: 'profile', component: ProfileComponent, canActivate: [authGuard] },
  { path: 'cart', component: CartComponent, canActivate: [authGuard] },
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupComponent },
  { path: 'menu', component: MenuComponent },
  { path: 'checkout', component: CheckoutComponent },
  { path: 'aboutus', component: AboutUsComponent },
  { path: 'contactus', component: HelpPageComponent },
  { path: 'navbar', component: NavbarComponent },
  { path: 'footer', component: FooterComponent },

  // Restaurant routes
  { path: 'restaurantLogin', component: RestaurantLoginComponent },
  { path: 'restaurantSignup', component: RestaurantSignupComponent },
  { path: 'restaurantDashboard', component: RestaurantDashboardComponent },


  // Admin routes
  { path: 'adminLogin', component: AdminLoginComponent },
  { path: 'adminDashboard', component: AdminDashboardComponent },
  { path: 'admin-dashboard', component: AdminDashboardComponent },
  { path: 'add-header', component: AdminHeaderComponent },
  { path: 'admin-footer', component: AdminFooterComponent },
  { path: 'admin-orders', component: AdminOrdersComponent },
  { path: 'agent-dashboard/:id', component: AgentDashboardComponent },
  { path: 'agent-details', component: AgentDetailsComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }