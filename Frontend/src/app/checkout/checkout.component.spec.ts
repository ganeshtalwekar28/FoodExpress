import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core'; 

import { CheckoutComponent } from './checkout.component';
import { OrderService } from '../orders.service';
import { CartService } from '../cart.service';
import { AuthService } from '../auth.service';
import { User } from '../user';

class MockOrderService {
  getCustomer(id: number) {
    
    return of(null);
  }
  getCart(id: number) {
    return of(null);
  }
}

class MockCartService {}

class MockAuthService {
  getUserId() {
    return 1; 
  }
}

describe('CheckoutComponent', () => {
  let component: CheckoutComponent;
  let fixture: ComponentFixture<CheckoutComponent>;
  let orderService: OrderService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CheckoutComponent],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        ReactiveFormsModule
      ],
      providers: [
        { provide: OrderService, useClass: MockOrderService },
        { provide: CartService, useClass: MockCartService },
        { provide: AuthService, useClass: MockAuthService }
      ],
      schemas: [NO_ERRORS_SCHEMA] 
    }).compileComponents();

    fixture = TestBed.createComponent(CheckoutComponent);
    component = fixture.componentInstance;
    orderService = TestBed.inject(OrderService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  
  describe('#calculateFinalTotal', () => {
    it('should correctly calculate the grand total from a subtotal', () => {
      
      component.cartDetails = { totalAmount: 100 };
      component.gstPercentage = 0.18;
      component.deliveryFee = 0;

      
      component.calculateFinalTotal();

      
      expect(component.totalAmountWithGST).toBe(118);
    });
  });

  
  describe('#fetchCustomerData', () => {
    it('should handle customers with no addresses gracefully', () => {
      
      const mockCustomer: User = {
        id: 1,
        name: 'Test User',
        email: 'test@test.com',
        phone: '1234567890',
        password: '',
        termsAccepted: true,
      };
      spyOn(orderService, 'getCustomer').and.returnValue(of(mockCustomer));
      spyOn(console, 'warn'); 

      
      fixture.detectChanges(); 
      component.fetchCustomerData(1);

      
      expect(console.warn).toHaveBeenCalledWith('Customer data or address list is empty/invalid. Cannot prefill form.');
    });
    
    it('should handle customers with an empty address array gracefully', () => {
      
      const mockCustomer: User = {
        id: 1,
        name: 'Test User',
        email: 'test@test.com',
        phone: '1234567890',
        password: '',
        termsAccepted: true,
      };
      spyOn(orderService, 'getCustomer').and.returnValue(of(mockCustomer));
      spyOn(console, 'warn');

      
      fixture.detectChanges(); 
      component.fetchCustomerData(1);

      
      expect(console.warn).toHaveBeenCalledWith('Customer data or address list is empty/invalid. Cannot prefill form.');
    });
  });
});