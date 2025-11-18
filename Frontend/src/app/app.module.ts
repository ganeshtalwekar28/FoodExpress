import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { RestaurantComponent } from './restaurant/restaurant.component';
import { ProfileComponent } from './profile/profile.component';
import { CartComponent } from './cart/cart.component';
import { MenuComponent } from './menu/menu.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { LoginComponent } from './login/login.component';
import { SignupComponent } from './signup/signup.component';
import { RestaurantDashboardComponent } from './restaurant-dashboard/restaurant-dashboard.component';
import { RestaurantLoginComponent } from './restaurant-login/restaurant-login.component';
import { RestaurantSignupComponent } from './restaurant-signup/restaurant-signup.component';
import { RestaurantNavbarComponent } from './restaurant-navbar/restaurant-navbar.component';
import { AdminHeaderComponent } from './admin-header/admin-header.component';
import { AdminFooterComponent } from './admin-footer/admin-footer.component';
import { AdminOrdersComponent } from './admin-orders/admin-orders.component';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';
import { AgentDetailsComponent } from './agent-details/agent-details.component';
import { AgentDashboardComponent } from './agent-dashboard/agent-dashboard.component';
import { FooterComponent } from './footer/footer.component';
import { NavbarComponent } from './navbar/navbar.component';
import { AdminLoginComponent } from './admin-login/admin-login.component';
import { CheckoutComponent } from './checkout/checkout.component';
import { HelpPageComponent } from './help-page/help-page.component';
import { AboutUsComponent } from './about-us/about-us.component';
import { MatSnackBarModule } from '@angular/material/snack-bar';


@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    RestaurantComponent,
    ProfileComponent,
    CartComponent,
    CheckoutComponent,
    MenuComponent,
    RestaurantDashboardComponent,
    RestaurantLoginComponent,
    RestaurantSignupComponent,
    LoginComponent,
    SignupComponent,
    RestaurantNavbarComponent,
    AdminHeaderComponent,
    AdminFooterComponent,
    AdminOrdersComponent,
    AdminDashboardComponent,
    AgentDetailsComponent,
    AgentDashboardComponent,
    FooterComponent,
    NavbarComponent,
    AdminLoginComponent,
    HelpPageComponent,
    AboutUsComponent

  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    MatSnackBarModule

  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
