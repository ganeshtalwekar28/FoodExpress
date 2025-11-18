import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { RestaurantOwnerService } from './restaurant-owner.service';

describe('RestaurantOwnerService', () => {
  let service: RestaurantOwnerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(RestaurantOwnerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
