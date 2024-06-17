import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CarousselimagesComponent } from './carousselimages.component';

describe('CarousselimagesComponent', () => {
  let component: CarousselimagesComponent;
  let fixture: ComponentFixture<CarousselimagesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CarousselimagesComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CarousselimagesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
